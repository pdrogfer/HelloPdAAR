package com.pdrogfer.hellopdaar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    PdUiDispatcher pdUiDispatcher;

    private static final String TAG = "HELLO_PD";
    Switch onOffSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initPD();
            loadPDPatch();
        } catch (IOException e) {
            Log.e(TAG, "onCreate: ERROR", e);
            finish();
        }
        initGUI();
    }

    private void loadPDPatch() throws IOException {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.hello_pd_patch), dir, true);
        File pdPatch = new File(dir, "hello_pd_patch.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        pdUiDispatcher = new PdUiDispatcher();
        PdBase.setReceiver(pdUiDispatcher);
    }

    private void initGUI() {
        onOffSwitch = (Switch) findViewById(R.id.sw_onOff);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: value = " + isChecked);
                float floatState = (isChecked) ? 1.0f : 0.0f;
                PdBase.sendFloat("onOff", floatState);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
    }
}
