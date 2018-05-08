package it.LucaAngioloni;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EqualizerParallel {
    private static boolean launchThreads(int thread_number, Thread[] threads){
        for (int i = 0; i < thread_number; i++) {
            threads[i].start();
        }

        for (int i = 0; i < thread_number; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
    private static boolean launchThreadsPool(int thread_number, Thread[] threads){
        ExecutorService e = Executors.newFixedThreadPool(thread_number);
        for (int i = 0; i < thread_number; i++) {
            e.execute(threads[i]);
        }
        e.shutdown();
        while(!e.isTerminated()) {
        }

       /* try {
            // Wait a while for existing tasks to terminate
            if (!e.awaitTermination(10, TimeUnit.SECONDS)) {
                e.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!e.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                    return false;
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            e.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            return false;
        }*/

        return true;
    }

    static float[] equalize(float[] v, int size){

        //Number of threads the processor can handle at once
        //If the processor supports virtualization, this is 2*cores
        int threads = Runtime.getRuntime().availableProcessors();

        PartialLookupThread[] partialLookupThreads = new PartialLookupThread[threads];

        int size_per_thread = size/threads;
        int bins_per_thread = 256/threads;

        for (int i = 0; i < threads-1; i++) {
            int start_t = i*size_per_thread;
            int end_t = i*size_per_thread + size_per_thread;

            partialLookupThreads[i] = new PartialLookupThread(v, start_t, end_t, size);
        }

        partialLookupThreads[threads-1] = new PartialLookupThread(v, (threads-1)*size_per_thread, size, size);

        //launch threads
        launchThreads(threads, partialLookupThreads);
        //finished threads

        LookupAdder[] adders = new LookupAdder[threads];
        float[] lookup = new float[256];
        float [][] partialLookups = new float[threads][];

        for (int i = 0; i < threads; i++) {
            partialLookups[i] = partialLookupThreads[i].getPartialLookup();
        }

        for (int i = 0; i < threads-1; i++) {
            int start_h = i*bins_per_thread;
            int end_h = i*bins_per_thread + bins_per_thread;

            adders[i] = new LookupAdder(lookup, partialLookups, start_h, end_h, threads);
        }
        adders[threads-1] = new LookupAdder(lookup, partialLookups, (threads-1)*bins_per_thread, 256, threads);

        //launch threads
        launchThreads(threads, adders);
        //finished threads

        float[] newV = new float[size];
        
        EqualizedValuesCalculator[] equalizers = new EqualizedValuesCalculator[threads];

        for (int i = 0; i < threads-1; i++) {
            int start_t = i*size_per_thread;
            int end_t = i*size_per_thread + size_per_thread;

            equalizers[i] = new EqualizedValuesCalculator(v, start_t, end_t, lookup, newV);
        }

        equalizers[threads-1] = new EqualizedValuesCalculator(v, (threads-1)*size_per_thread, size, lookup, newV);

        //launch threads
        launchThreads(threads, equalizers);
        //finished threads

        return newV;
    }
}
