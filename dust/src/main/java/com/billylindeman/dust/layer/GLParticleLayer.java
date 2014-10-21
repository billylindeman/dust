package com.billylindeman.dust.layer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.billylindeman.dust.particle.Emitter;
import com.billylindeman.dust.particle.Particle;
import com.billylindeman.dust.util.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);

//        setEGLContextClientVersion(2);
//        setPreserveEGLContextOnPause(true);

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
        Map<Emitter,TexturedRect> texturedRectMap = new HashMap<Emitter,TexturedRect>();
        long lastFrame = SystemClock.elapsedRealtime();

        Vector2 size = new Vector2();

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            gl.glViewport(0, 0, w, h);
            gl.glOrthof(0,320,0,480,0,1);

            size.x  =w;
            size.y=h;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            float delta = getFrameDeltaTime();

            if(emitters != null) {
                for(Emitter emitter : emitters) {
                    if(!texturedRectMap.containsKey(emitter)) {
                        TexturedRect tr = new TexturedRect(gl, emitter.getConfig().texture);
                        texturedRectMap.put(emitter,tr);
                    }
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

            TexturedRect tr = texturedRectMap.get(e);

            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

            for(int i=0; i<count ; i++) {
                Particle p = particles[i];
                gl.glPushMatrix();
                gl.glTranslatef(p.position.x, p.position.y, 0);
                gl.glScalef(.5f*p.particleSize, .5f*p.particleSize, 0);
                gl.glRotatef((float)p.angle, 0,0,1);
                tr.draw(gl, p.color);
                gl.glPopMatrix();
            }
        }

    }





}
