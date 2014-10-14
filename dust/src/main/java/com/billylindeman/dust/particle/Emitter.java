package com.billylindeman.dust.particle;

/**
 * This is a particle emitter class
 */
public class Emitter {
    EmitterConfig config;


    boolean active;
    Particle[] particles;

    float emissionRate;
    float emitCounter;
    float elapsedTime;
    int particleCount;
    int particleIndex;

    public Emitter(EmitterConfig c){
        config = c;

        emissionRate = config.maxParticles / config.particleLifespan;
        emitCounter = 0;
    }

    public void updateWithDelta(float delta) {
        if(active && emissionRate > 0) {
            float rate = 1.0f / emissionRate;

            if(particleCount < config.maxParticles) {
                emitCounter += delta;
            }

            while(particleCount < config.maxParticles && emitCounter > rate) {
                addParticle();
                emitCounter -= rate;
            }
            elapsedTime += delta;

            if(config.duration != -1 && config.duration < elapsedTime) {
                stopEmission();
            }
        }

        particleIndex = 0;

        while(particleIndex < particleCount) {

        }

    }

    private void addParticle() {

    }

    public void stopEmission() {

    }

}
