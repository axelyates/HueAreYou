package com.example.ayates2.hueareyou;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;


public class RandomColorButton extends AppCompatActivity {

    private PHHueSDK phHueSDK;
    private static final int MAX_HUE = 65535;
    public static final String TAG = "HueAreYou";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();
        Button randomButton;
        randomButton = (Button) findViewById(R.id.buttonRand);
        randomButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                randomLights();
            }
        });
    }

    public void randomLights(){
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();

        for(PHLight light : allLights){
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));

            //to validate your lightstate is valid (before sending to bridge you can use:
            //String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            //bridge.updateLightState(light, lightState);
        }
    }

    PHLightListener listener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}

        @Override
        public void onSuccess() {}

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }
    };

    @Override
    protected void onDestroy(){
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if(bridge != null){
            if(phHueSDK.isHeartbeatEnabled(bridge)){
                phHueSDK.disableHeartbeat(bridge);
            }
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}