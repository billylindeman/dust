package com.billylindeman.dust.util;

/**
 *
 */
public class Color {
    public float r;
    public float g;
    public float b;
    public float a;

    public Color() {

    }

    public Color(float red, float green, float blue, float alpha) {
        r = red;
        g = green;
        b = blue;
        a = alpha;
    }

    public String toString() {
        return "Color(" + r + "," + g + "," + b + "," + a + ")";
    }

//    public int getPacked() {
//        return (alpha << 24) | (red << 16) | (green << 8) | blue;
//    }

}
