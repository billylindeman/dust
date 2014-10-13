package com.billylindeman.dust.util;

/**
 *
 */
public class Color {
    byte red;
    byte green;
    byte blue;
    byte alpha;

    public Color() {

    }

    public Color(float red, float green, float blue, float alpha) {

    }


    public int getPacked() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

}
