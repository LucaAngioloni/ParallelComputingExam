package it.LucaAngioloni;


import java.util.Arrays;

public class EqualizerSerial {
    static float[] equalize(float[] v, int size){


        //Histogram computation
        int[] histogram = new int[256];
        for (int i = 0; i < size; i++){
            int value = Math.max(0, Math.min(255, Math.round(v[i]*255)));
            histogram[value]++;
        }


        //Cumulative histogram computation (cumulative distribution function)
        int[] chistogram = new int[256];
        chistogram[0] = histogram[0];
        for(int i=1;i<256;i++){
            chistogram[i] = chistogram[i-1] + histogram[i];
        }

        //New value for each bin
        float[] lookup = new float[256];
        for(int i=0;i<256;i++){
            lookup[i] =  ((float)1/(float)255)*(float)((chistogram[i]*255.0)/(float)size);
        }

        float[] newV = new float[size];

        for (int i = 0; i<size; i++){
            int value = Math.max(0, Math.min(255, Math.round(v[i]*255)));
            newV[i] = Math.max(0, Math.min(1, lookup[value]));
        }

        return newV;
    }
}
