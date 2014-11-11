package com.billylindeman.dust.layer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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
    Vector2 glSize = new Vector2();


    public GLParticleLayer(Context context) {
        super(context);
        init();
    }

    public GLParticleLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GLParticleLayer", "Pausing particle layer");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GLParticleLayer", "Resuming particle layer");


    }

    private void init() {
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

    public Vector2 getLayerPositionForView(View v) {
        /** Load locations for view and layer */
        int[] viewLocationInWindow = new int[2];
        int[] layerLocationInWindow = new int[2];
        v.getLocationInWindow(viewLocationInWindow);
        getLocationInWindow(layerLocationInWindow);

        /** Use difference of view location and layer location */
        Vector2 locationInWindow = new Vector2(viewLocationInWindow[0],viewLocationInWindow[1]);
        Vector2 layerLocation = new Vector2(layerLocationInWindow[0],layerLocationInWindow[1]);

        Vector2 locationInLayer = Vector2.subtract(locationInWindow,layerLocation);

        Vector2 glCoordinateForLocationInLayer = new Vector2();
        Rect window = new Rect();
        getWindowVisibleDisplayFrame(window);

        glCoordinateForLocationInLayer.x = (locationInLayer.x / window.width()) * glSize.x;
        glCoordinateForLocationInLayer.y = ( (1- (locationInLayer.y / window.height())) * glSize.y);

        return glCoordinateForLocationInLayer;

    }

    class ParticleRenderer implements Renderer {
        ArrayList<Emitter> emitters = new ArrayList<Emitter>();
        Map<Emitter,TexturedRect> texturedRectMap = new HashMap<Emitter,TexturedRect>();
        long lastFrame = SystemClock.elapsedRealtime();

        Vector2 size = new Vector2();
        public ParticleRenderer() {
            setPreserveEGLContextOnPause(true);
            setZOrderOnTop(true);
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            getHolder().setFormat(PixelFormat.RGBA_8888);

        }


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
            Log.d("GL", "surface created");
            texturedRectMap = new HashMap<Emitter, TexturedRect>();
            for(Emitter e : emitters) {
                TexturedRect tr = new TexturedRect();
                tr.registerTextureHandle(gl, e.getConfig().texture);
                texturedRectMap.put(e,tr);
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            Log.d("GL", "surface changed");

            gl.glViewport(0, 0, w, h);

            glSize.x = 320;
            glSize.y = ((float)h/(float)w)*320;
            gl.glOrthof(0,glSize.x,0,glSize.y,0,1);

            size.x=w;
            size.y=h;

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            float delta = getFrameDeltaTime();

            if(emitters != null) {
                Log.d("GL", "in emitters");
                for(Emitter emitter : emitters) {
                    emitter.updateWithDelta(delta);
                    drawParticlesForEmitter(gl, emitter);
                }
            }

            int glerror = gl.glGetError();
            if(glerror !=0) Log.d("Error", GLUtils.getEGLErrorString(glerror));
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
            Log.d("GL", "drawing particles for emitter with tr:" + tr);

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
