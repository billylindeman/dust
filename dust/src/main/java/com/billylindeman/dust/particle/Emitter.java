package com.billylindeman.dust.particle;

/**
 * This is a particle emitter class
 */
public class Emitter {
    EmitterConfig config;
    boolean active;
    Particle[] particles;


    public Emitter(EmitterConfig c){
        config = c;
    }

}
