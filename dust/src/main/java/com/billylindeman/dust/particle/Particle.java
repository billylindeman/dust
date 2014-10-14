package com.billylindeman.dust.particle;

import android.graphics.PointF;

import java.util.Random;

/**
 *
 */
public class Particle {
    static Random r = new Random();


    private EmitterConfig config;

    public PointF position;
    public PointF direction;
    public PointF startPos;
    public int color;
    public int deltaColor;
    public float rotation;
    public float rotationDelta;
    public float radialAcceleration;
    public float tangentialAcceleration;
    public float radius;
    public float radiusDelta;
    public float angle;
    public float degreesPerSecond;
    public float particleSize;
    public float particleSizeDelta;
    public float timeToLive;

    public Particle(EmitterConfig c) {
        config = c;
        init();
    }

    public void init() {
        //init the position of the particle
        position.x = config.sourcePosition.x + config.sourcePositionVariance.x * randomMinusOneToOne();
        position.y = config.sourcePosition.y + config.sourcePositionVariance.y * randomMinusOneToOne();
        startPos.x = config.sourcePosition.x;
        startPos.y = config.sourcePosition.y;


        //Init the direction of particle
        angle = config.angle + config.angleVariance * randomMinusOneToOne();

        PointF vector = new PointF()

    }


    private static float randomMinusOneToOne() {
        return (r.nextFloat() * 2 ) - 1;
    }

}
