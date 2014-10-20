package com.billylindeman.dust.particle;

import com.billylindeman.dust.util.Vector2;

/**
 * This is a particle emitter class
 */
public class Emitter {
    public final int kParticleTypeGravity = 0;
    public final int kParticleTypeRadial = 1;

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

        particles = new Particle[config.maxParticles];
        for(int i=0; i<config.maxParticles; i++) {
            particles[i] = new Particle(config);
        }

        emissionRate = (float)config.maxParticles / config.particleLifespan;
        emitCounter = 0;
        active = true;

    }

    public synchronized void updateWithDelta(float delta) {
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
            Particle particle = particles[particleIndex];

            particle.timeToLive -= delta;

            /** If particle is still alive, lets update it */
            if(particle.timeToLive > 0) {

                /** Update positions based on emitter type*/
                if(config.emitterType == kParticleTypeRadial) {
                    particle.angle += particle.degreesPerSecond*delta;
                    particle.radius += particle.radiusDelta*delta;

                    /** update position */
                    particle.position.x = config.sourcePosition.x - (float)Math.cos((float)particle.angle) * particle.radius;
                    particle.position.y = config.sourcePosition.y - (float)Math.sin((float)particle.angle) * particle.radius;

                }else if (config.emitterType == kParticleTypeGravity) {
                    Vector2 tmp, radial,tangential;

                    radial = new Vector2(0,0);

                    Vector2 positionDifference = Vector2.subtract(particle.startPos, new Vector2());
                    particle.position = Vector2.subtract(particle.position, positionDifference);

                    if(particle.position.x != 0 || particle.position.y != 0) {
                        radial = Vector2.normalize(particle.position);
                    }

                    tangential = radial;
                    radial = Vector2.multiplyScalar(radial, particle.radialAcceleration);
                    float newy = tangential.x;
                    tangential.x = -tangential.y;
                    tangential.y = newy;
                    tangential = Vector2.multiplyScalar(tangential, particle.tangentialAcceleration);

                    tmp = Vector2.add(Vector2.add(radial,tangential), config.gravity);
                    tmp = Vector2.multiplyScalar(tmp, delta);
                    particle.direction = Vector2.add(particle.direction, tmp);
                    tmp  = Vector2.multiplyScalar(particle.direction, delta);

                    particle.position = Vector2.add(particle.position, tmp);
                    particle.position = Vector2.add(particle.position, positionDifference);
                }

                /** update particles color */
                particle.color.r += particle.deltaColor.r * delta;
                particle.color.g += particle.deltaColor.g * delta;
                particle.color.b += particle.deltaColor.b * delta;
                particle.color.a += particle.deltaColor.a * delta;

                /** update particle size */
                particle.particleSize += particle.particleSizeDelta * delta;
                particle.particleSize = Math.max(0, particle.particleSize);

                /** update rotation */
                particle.rotation += particle.rotationDelta *delta;

                particleIndex++;
            }else {
                /** particle is dead */
                if(particleIndex != particleCount -1) {
                    /** swap this particle with the last particle alive in our alive count
                     * this keeps our particles[] array packed with alive particles first
                     */
                    Particle lastAlive = particles[particleCount -1];
                    Particle thisParticle = particles[particleIndex];
                    particles[particleIndex] = lastAlive;
                    particles[particleCount-1] = thisParticle;
                }
                particleCount--;
            }

        }

    }

    private synchronized boolean addParticle() {
        if(particleCount == config.maxParticles) {
            return false;
        }

        Particle p = particles[particleCount];
        p.init();

        particleCount++;
        return true;
    }

    public synchronized void stopEmission() {
        active = false;
        elapsedTime = 0;
        emitCounter = 0;
    }

    public synchronized void reset() {
        active = true;
        elapsedTime =0;
        for(int i=0; i<particleCount; i++) {
            particles[i].init();
            particles[i].timeToLive = 0;
        }
        emitCounter = 0;
        emissionRate = (float)config.maxParticles / config.particleLifespan;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public int getParticleCount() {
        return particleCount;
    }

}
