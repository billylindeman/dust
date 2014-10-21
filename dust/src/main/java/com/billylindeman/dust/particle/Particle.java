package com.billylindeman.dust.particle;

import com.billylindeman.dust.util.Color;
import com.billylindeman.dust.util.Vector2;

import java.util.Random;

/**
 *
 */
public class Particle {
    static Random r = new Random();

    private EmitterConfig config;

    public Vector2 position = new Vector2();
    public Vector2 direction = new Vector2();
    public Vector2 startPos = new Vector2();
    public Color color = new Color();
    public Color endColor = new Color();
    public Color deltaColor = new Color();
    public float rotation;
    public float rotationDelta;
    public float radialAcceleration;
    public float tangentialAcceleration;
    public float radius;
    public float radiusDelta;
    public double angle;
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
        float newAngle = config.angle + config.angleVariance * randomMinusOneToOne();
        float vectorSpeed = config.speed + config.speedVariance * randomMinusOneToOne();

        // calculate direction from angle'd vector multiplied by scalar speed
        direction.x = (float)Math.cos(Math.toRadians(newAngle));
        direction.y = (float)Math.sin(Math.toRadians(newAngle));
        direction.multiplyScalar(vectorSpeed);

        timeToLive = Math.max(0, config.particleLifespan + config.particleLifespanVariance*randomMinusOneToOne());

        float startRadius = config.maxRadius + config.maxRadiusVariance*randomMinusOneToOne();
        float endRadius = config.minRadius + config.minRadiusVariance * randomMinusOneToOne();

        radius = startRadius;
        radiusDelta = (endRadius - startRadius) / timeToLive;
        angle = config.angle + config.angleVariance*randomMinusOneToOne();
        degreesPerSecond = config.rotatePerSecond + config.rotatePerSecondVariance*randomMinusOneToOne();

        radialAcceleration = config.radialAcceleration+config.radialAccelVariance*randomMinusOneToOne();
        tangentialAcceleration = config.tangentialAcceleration + config.tangentialAccelVariance*randomMinusOneToOne();

        float particleStartSize = config.startParticleSize + config.startParticleSizeVariance*randomMinusOneToOne();
        float particleFinishSize = config.finishParticleSize + config.finishParticleSizeVariance*randomMinusOneToOne();
        particleSize = Math.max(0, particleStartSize);
        particleSizeDelta = ((particleFinishSize - particleStartSize) / timeToLive);


        color.r = config.startColor.r + config.startColorVariance.r*randomMinusOneToOne();
        color.g = config.startColor.g + config.startColorVariance.g*randomMinusOneToOne();
        color.b = config.startColor.b + config.startColorVariance.b*randomMinusOneToOne();
        color.a = config.startColor.a + config.startColorVariance.a*randomMinusOneToOne();

        endColor.r = config.finishColor.r + config.finishColorVariance.r*randomMinusOneToOne();
        endColor.g = config.finishColor.g + config.finishColorVariance.g*randomMinusOneToOne();
        endColor.b = config.finishColor.b + config.finishColorVariance.b*randomMinusOneToOne();
        endColor.a = config.finishColor.a + config.finishColorVariance.a*randomMinusOneToOne();

        deltaColor.r = ((endColor.r - color.r) / timeToLive);
        deltaColor.g = ((endColor.g - color.g) / timeToLive);
        deltaColor.b = ((endColor.b - color.b) / timeToLive);
        deltaColor.a = ((endColor.a - color.a) / timeToLive);


        float startA = config.rotationStart + config.rotationStartVariance*randomMinusOneToOne();
        float endA = config.rotationEnd + config.rotationEndVariance*randomMinusOneToOne();

        rotation=startA;
        rotationDelta = (endA - startA) / timeToLive;

    }


    private static float randomMinusOneToOne() {
        return (r.nextFloat() * 2 ) - 1;
    }

}
