package it.LucaAngioloni;

public class PartialLookupThread extends Thread {
    private float[] lookup;
    private final float[] v;
    private final int start;
    private final int end;
    private final int size;

    public float[] getPartialLookup() {
        return lookup;
    }

    PartialLookupThread(float [] v, int start, int end, int size){
        this.v = v;
        this.start = start;
        this.end = end;
        this.size = size;
        this.lookup = new float[256];
    }

    @Override
    public void run() {
        //Histogram computation
        int[] histogram = new int[256];
        for (int i = start; i < end; i++){
            int value = Math.max(0, Math.min(255, Math.round(v[i]*255)));
            histogram[value]++;
        }

        //Cumulative histogram computation (cumulative distribution function)
        int[] chistogram = new int[256];
        chistogram[0] = histogram[0];
        lookup[0] =  ((float)1/(float)255)*(float)((chistogram[0]*255.0)/(float)size);
        for(int i=1;i<256;i++){
            chistogram[i] = chistogram[i-1] + histogram[i];
            //New value for each bin
            lookup[i] =  ((float)1/(float)255)*(float)((chistogram[i]*255.0)/(float)size);
        }
    }
}
