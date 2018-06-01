// MIT License

// Copyright (c) 2018 SqrtPapere and Luca Angioloni

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

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
