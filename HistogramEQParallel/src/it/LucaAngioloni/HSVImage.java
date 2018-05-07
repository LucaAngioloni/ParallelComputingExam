package it.LucaAngioloni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HSVImage {
    private float[] H;
    private float[] S;
    private float[] V;
    private int width;
    private int height;

    HSVImage(BufferedImage src){
        int[] originalPixels = src.getRGB(0,0, src.getWidth(), src.getHeight(), null, 0, src.getWidth());
        float[] hsvPixelsH = new float[src.getWidth()*src.getHeight()];
        float[] hsvPixelsS = new float[src.getWidth()*src.getHeight()];
        float[] hsvPixelsV = new float[src.getWidth()*src.getHeight()];

        float[] hsb = new float[]{0,0,0};
        for(int i = 0; i < originalPixels.length; i++) {
            Color c = new Color(originalPixels[i]);
            int red = c.getRed();
            int green = c.getGreen();
            int blue = c.getBlue();

            hsb = Color.RGBtoHSB(red, green, blue, null);

            hsvPixelsH[i] = hsb[0];
            hsvPixelsS[i] = hsb[1];
            hsvPixelsV[i] = hsb[2];
        }

        this.H = hsvPixelsH;
        this.S = hsvPixelsS;
        this.V = hsvPixelsV;

        this.width = src.getWidth();
        this.height = src.getHeight();
    }

    HSVImage(float[] H, float[] S, float[] V, int width, int height){
        this.H = H;
        this.S = S;
        this.V = V;
        this.width = width;
        this.height = height;
    }

    public float[] getH() {
        return H;
    }

    public void setH(float[] h) {
        H = h;
    }

    public float[] getS() {
        return S;
    }

    public void setS(float[] s) {
        S = s;
    }

    public float[] getV() {
        return V;
    }

    public void setV(float[] v) {
        V = v;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getRGBImage(){
        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int rgb;

        for(int i = 0; i < height; i++){
            for (int j = 0; j<width; j++){
                rgb = Color.HSBtoRGB(H[i*width + j], S[i*width + j], V[i*width + j]);

                ret.setRGB(j, i, rgb);
            }
        }

        return ret;
    }

    public int getNumPixels(){
        return width*height;
    }
}
