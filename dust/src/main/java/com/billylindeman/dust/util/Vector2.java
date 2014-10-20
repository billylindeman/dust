package com.billylindeman.dust.util;

/**
 *
 */
public class Vector2 {
    public float x = 0;
    public float y = 0;

    public Vector2() {}
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float length() {
        double x= this.x;
        double y = this.y;
        return (float)Math.sqrt(x*x +y*y);
    }

    public static Vector2 normalize(Vector2 in) {
        float scale = 1.0f / in.length();
        return new Vector2(in.x*scale, in.y*scale);
    }

    public static Vector2 multiplyScalar(Vector2 vec, float scalar) {
        return new Vector2(vec.x*scalar, vec.y*scalar);
    }

    public static Vector2 subtract(Vector2 left, Vector2 right) {
        return new Vector2(left.x-right.x,left.y-right.y);
    }

    public static Vector2 add(Vector2 left, Vector2 right) {
        return new Vector2(left.x+right.x,left.y+right.y);
    }
}
