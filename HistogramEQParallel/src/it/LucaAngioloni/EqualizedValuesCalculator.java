package it.LucaAngioloni;

public class EqualizedValuesCalculator extends Thread {
    private final float[] v;
    private final int start_t;
    private final int end_t;
    private final float[] lookup;
    private final float[] newV;

    EqualizedValuesCalculator(float[] v, int start_t, int end_t, float[] lookup, float[] newV){
        this.v = v;
        this.start_t = start_t;
        this.end_t = end_t;
        this.lookup = lookup;
        this.newV = newV;
    }

    public float[] getNewV() {
        return newV;
    }

    @Override
    public void run() {
        for (int i = start_t; i < end_t; i++) {
            int value = Math.max(0, Math.min(255, Math.round(v[i]*255)));
            newV[i] = Math.max(0, Math.min(1, lookup[value]));
        }
    }
}
