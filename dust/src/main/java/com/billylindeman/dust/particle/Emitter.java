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

        active = false;
        reset();

    }

    Vector2 tmp = new Vector2(),
            radial = new Vector2(),
            tangential= new Vector2(),
            positionDifference = new Vector2();

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

                    radial.zero();

                    positionDifference.zero().clone(particle.startPos);
                    particle.position.subtract(positionDifference);

                    if(particle.position.x != 0 || particle.position.y != 0) {
                        radial.clone(particle.position).normalize();
                    }

                    tangential.clone(radial);
                    radial.multiplyScalar(particle.radialAcceleration);

                    float newy = tangential.x;
                    tangential.x = -tangential.y;
                    tangential.y = newy;
                    tangential.multiplyScalar(particle.tangentialAcceleration);


                    tmp.zero();
                    tmp.add(radial).add(tangential).add(config.gravity);
                    tmp.multiplyScalar(delta);
                    particle.direction.add(tmp);

                    tmp.clone(particle.direction).multiplyScalar(delta);

                    particle.position.add(tmp);
                    particle.position.add(positionDifference);
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

    public synchronized void startEmission() {
        active=true;
    }

    public synchronized void reset() {
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

    public EmitterConfig getConfig() {
        return config;
    }

    public boolean isActive() {
        return active;
    }

}
