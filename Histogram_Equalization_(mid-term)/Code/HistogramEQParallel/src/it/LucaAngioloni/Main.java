package it.LucaAngioloni;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Options options = new Options();

        Option path = new Option("p", "path", true, "input image path");
        path.setRequired(true);
        options.addOption(path);

        Option threads = new Option("t", "threads", true, "number of threads (default platform dependent and system dependend)");
        threads.setRequired(false);
        options.addOption(threads);

        Option json_out = new Option("j", "json", false, "json output if present");
        json_out.setRequired(false);
        options.addOption(json_out);

        Option seral_alg = new Option("s", "serial", false, "if present serial algorithm (if no serial and no parallel then both)");
        seral_alg.setRequired(false);
        options.addOption(seral_alg);

        Option parallel_alg = new Option("par", "parallel", false, "if present parallel algorithm (if no serial and no parallel then both)");
        parallel_alg.setRequired(false);
        options.addOption(parallel_alg);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        int thread_num = 0;

        String image_path = cmd.getOptionValue("path");
        if (cmd.hasOption("threads")) {
            thread_num = Integer.parseInt(cmd.getOptionValue("threads"));
        }
        boolean json = cmd.hasOption("json");
        boolean serial = cmd.hasOption("serial");
        boolean parallel = cmd.hasOption("parallel");

        File f1 = new File(image_path);

        BufferedImage img;
        try {
            img = ImageIO.read(f1);
        } catch (IOException e) {
            e.printStackTrace();
            img = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        }

        HSVImage hsv_img = new HSVImage(img);

        long start;
        long duration_serial = 0;
        long duration_parallel = 0;
        float[] newV_serial = new float[256];
        float[] newV_parallel = new float[256];

        if (serial || !parallel) {
            //SERIAL
            //start timer
            start = System.nanoTime();
            newV_serial = EqualizerSerial.equalize(hsv_img.getV(), hsv_img.getNumPixels());
            duration_serial = System.nanoTime() - start;
            //stop timer
        }

        if (parallel || !serial){

            //PARALLEL
            start = System.nanoTime();
            newV_parallel = EqualizerParallel.equalize(hsv_img.getV(), hsv_img.getNumPixels(), thread_num);
            duration_parallel = System.nanoTime() - start;
            //stop timer
        }

        double seconds_serial = (double)duration_serial / 1000000000.0;

        String output = "{";

        if (serial || !parallel) {
            if (!json) {

                System.out.println("The Serial equalization algorithm took average: " + seconds_serial + " s");

                hsv_img.setV(newV_serial);

                BufferedImage equalized = hsv_img.getRGBImage();

                File outputfile = new File("images/equalized_serial.png");
                try {
                    ImageIO.write(equalized, "png", outputfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                output = output + "\"time_serial\": " + Double.toString(seconds_serial);
            }
        }

        double seconds_parallel = (double)duration_parallel / 1000000000.0;

        if (parallel || !serial){
            if (!json){
                System.out.println("The Parallel equalization algorithm took average: " + seconds_parallel + " s");

                hsv_img.setV(newV_parallel);

                BufferedImage equalized_parallel = hsv_img.getRGBImage();

                File outputfile_parallel = new File("images/equalized_parallel.png");
                try {
                    ImageIO.write(equalized_parallel, "png", outputfile_parallel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (serial || !parallel){
                    output = output + ", ";
                }
                output = output + "\"time_parallel\": " + Double.toString(seconds_parallel);
            }
        }

        output = output + "}";

        if (json){
            System.out.println(output);
        }
    }
}