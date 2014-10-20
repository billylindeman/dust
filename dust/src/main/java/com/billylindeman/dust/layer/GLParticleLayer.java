package com.billylindeman.dust.layer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.billylindeman.dust.particle.Emitter;
import com.billylindeman.dust.particle.Particle;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 *
 */
public class GLParticleLayer extends GLSurfaceView {
    ParticleRenderer particleRenderer;

    public GLParticleLayer(Context context) {
        super(context);
        init();
    }

    public GLParticleLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        particleRenderer = new ParticleRenderer();
        setRenderer(particleRenderer);

        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public void addEmitter(final Emitter e) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                particleRenderer.addEmitter(e);
            }
        });
    }


    class ParticleRenderer implements Renderer {
        ArrayList<Emitter> emitters = new ArrayList<Emitter>();
        long lastFrame = SystemClock.elapsedRealtime();

        Rectangle r = new Rectangle();

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            gl.glViewport(0, 0, w, h);
            gl.glOrthof(0,360,0,480,0,1);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            if(emitters != null) {
                float delta = getFrameDeltaTime();
                for(Emitter emitter : emitters) {
                    emitter.updateWithDelta(delta);
                    drawParticlesForEmitter(gl, emitter);
                }
            }

        }

        public void addEmitter(Emitter e) {
            emitters.add(e);

        }

        private float getFrameDeltaTime() {
            long frameTime = SystemClock.elapsedRealtime();
            float delta = ((float)frameTime - (float)lastFrame) / 1000;
            lastFrame = frameTime;
            return delta;
        }

        private void drawParticlesForEmitter(GL10 gl, Emitter e) {
            int count = e.getParticleCount();
            Particle[] particles = e.getParticles();

            for(int i=0; i<count ; i++) {
                Particle p = particles[i];
                gl.glPushMatrix();
                gl.glTranslatef(p.position.x,p.position.y,0);
                gl.glColor4f(p.color.r,p.color.g,p.color.b,1.0f);
                gl.glScalef(p.particleSize,p.particleSize,0);
                r.draw(gl);
                gl.glPopMatrix();

            }
        }

    }





}
