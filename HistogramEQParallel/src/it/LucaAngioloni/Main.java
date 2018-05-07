package it.LucaAngioloni;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String image_path = "images/poppysat.jpg";
        File f1 = new File(image_path);

        BufferedImage img;
        try {
            img = ImageIO.read(f1);
        } catch (IOException e) {
            e.printStackTrace();
            img = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        }

        HSVImage hsv_img = new HSVImage(img);

        long total = 0;
        long start;
        long duration;
        double parz;
        float[] newV = new float [hsv_img.getNumPixels()];

        start = System.nanoTime();
        newV = EqualizerSerial.equalize(hsv_img.getV(), hsv_img.getNumPixels());
        duration = System.nanoTime() - start;
        //stop timer
        parz = (double) duration/1000000000.0;
        //System.out.println("duration " + parz);

        for (int i = 0; i<101;i++) {
            //start timer
            start = System.nanoTime();
            newV = EqualizerSerial.equalize(hsv_img.getV(), hsv_img.getNumPixels());
            duration = System.nanoTime() - start;
            //stop timer
                parz = (double) duration/1000000000.0;
                //System.out.println("duration " + parz);
                if (i!=0){
                    total += duration;
            }
        }

        double seconds = (double)total / 1000000000.0;
        seconds = seconds/100;
        System.out.println("The Serial equalization algorithm took average: " + seconds + " s");

        hsv_img.setV(newV);

        BufferedImage equalized = hsv_img.getRGBImage();

        File outputfile = new File("images/equalized_serial.png");
        try {
            ImageIO.write(equalized, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        total = 0;
        float[] newV_parallel = new float[hsv_img.getNumPixels()];
        for (int i = 0; i<101;i++) {
            //start timer
            start = System.nanoTime();
            newV_parallel = EqualizerParallel.equalize(hsv_img.getV(), hsv_img.getNumPixels());
            duration = System.nanoTime() - start;
            //stop timer
            parz = (double) duration/1000000000.0;
            //System.out.println("duration " + parz);
            if (i!=0){
                total += duration;
            }
        }


        seconds = (double)total / 1000000000.0;
        seconds = seconds/100;
        System.out.println("The Parallel equalization algorithm took average: " + seconds + " s");


        hsv_img.setV(newV_parallel);

        BufferedImage equalized_parallel = hsv_img.getRGBImage();

        File outputfile_parallel = new File("images/equalized_parallel.png");
        try {
            ImageIO.write(equalized_parallel, "png", outputfile_parallel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}