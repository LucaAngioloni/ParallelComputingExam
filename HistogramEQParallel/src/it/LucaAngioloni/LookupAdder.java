package it.LucaAngioloni;

public class LookupAdder extends Thread{
    private final float[] lookup;
    private final float[][] partialLookups;
    private final int start;
    private final int end;
    private final int num_partials;

    public float[] getLookup() {
        return lookup;
    }

    LookupAdder(float [] lookup, float [][] partialLookups, int start, int end, int num_partials){
        this.lookup = lookup;
        this.partialLookups = partialLookups;
        this.start = start;
        this.end = end;
        this.num_partials = num_partials;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            float tot = 0;
            for (int j = 0; j < num_partials; j++) {
                tot += partialLookups[j][i];
            }

            lookup[i] = tot;
        }
    }
}
