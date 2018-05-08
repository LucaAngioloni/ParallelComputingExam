package it.LucaAngioloni;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String image_path = "images/t2.jpg";
        File f1 = new File(image_path);

        BufferedImage img;
        try {
            img = ImageIO.read(f1);
        } catch (IOException e) {
            e.printStackTrace();
            img = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        }

        HSVImage hsv_img = new HSVImage(img);

        long total_serial = 0;
        long total_parallel = 0;
        long start;
        long duration_serial;
        long duration_parallel;
        double parz_serial;
        double parz_parallel;
        float[] newV_serial = new float[256];
        float[] newV_parallel = new float[256];

        for (int i = 0; i<101;i++) {
            //SERIAL
            //start timer
            start = System.nanoTime();
            newV_serial = EqualizerSerial.equalize(hsv_img.getV(), hsv_img.getNumPixels());
            duration_serial = System.nanoTime() - start;
            //stop timer
                parz_serial = (double) duration_serial/1000000000.0;
                //System.out.println("duration " + parz_serial);
            if (i!=0){
                total_serial += duration_serial;
            }

            //PARALLEL
            start = System.nanoTime();
            newV_parallel = EqualizerParallel.equalize(hsv_img.getV(), hsv_img.getNumPixels());
            duration_parallel = System.nanoTime() - start;
            //stop timer
            parz_parallel = (double) duration_parallel/1000000000.0;
            //System.out.println("duration " + parz);
            if (i!=0){
                total_parallel += duration_parallel;
            }
        }

        double seconds_serial = (double)total_serial / 1000000000.0;
        seconds_serial = seconds_serial/100;
        System.out.println("The Serial equalization algorithm took average: " + seconds_serial + " s");

        hsv_img.setV(newV_serial);

        BufferedImage equalized = hsv_img.getRGBImage();

        File outputfile = new File("images/equalized_serial.png");
        try {
            ImageIO.write(equalized, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        double seconds_parallel = (double)total_parallel / 1000000000.0;
        seconds_parallel = seconds_parallel/100;
        System.out.println("The Parallel equalization algorithm took average: " + seconds_parallel + " s");

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