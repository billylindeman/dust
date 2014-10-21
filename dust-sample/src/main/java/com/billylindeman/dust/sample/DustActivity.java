package com.billylindeman.dust.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.billylindeman.dust.layer.GLParticleLayer;
import com.billylindeman.dust.particle.Emitter;
import com.billylindeman.dust.particle.EmitterConfig;


public class DustActivity extends Activity {
    Button startStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust);

        EmitterConfig config = EmitterConfig.fromStream(getResources().openRawResource(R.raw.confetti));
        final Emitter confetti = new Emitter(config);
        config = EmitterConfig.fromStream(getResources().openRawResource(R.raw.starexplosion));
        final Emitter stars = new Emitter(config);
        config = EmitterConfig.fromStream(getResources().openRawResource(R.raw.staroutlineexplosion));
        final Emitter staroutlines = new Emitter(config);

        GLParticleLayer layer = (GLParticleLayer)findViewById(R.id.gl_particle_layer);
        layer.addEmitter(confetti);
        layer.addEmitter(stars);
        layer.addEmitter(staroutlines);

        startStop = (Button)findViewById(R.id.button);
        startStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stars.isActive()) {
                    startStop.setText("start");
                    stars.stopEmission();
                    confetti.stopEmission();
                    staroutlines.stopEmission();
                }else {
                    startStop.setText("stop");
                    stars.startEmission();
                    confetti.startEmission();
                    staroutlines.startEmission();
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dust, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
