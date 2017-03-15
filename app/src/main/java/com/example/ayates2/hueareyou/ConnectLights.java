package com.example.ayates2.hueareyou;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;



public class ConnectLights extends AppCompatActivity {



    PHHueSDK phHueSDK = PHHueSDK.getInstance();

    //Register the PHSDKListener to receive callbacks from the bridge.
    //phHueSDK.getNotificationManager().registerSDKListener(listener);

    //Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {
        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
            //Here you receive notifications that the BridgeResource Cache was updated
            //Use the PHMessageType to check which cache was updated, e.g.
            /*if(cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)){
                System.out.println("Lights Cache Updated");
            }*/
        }

        @Override
        public void onBridgeConnected(PHBridge phBridge, String s) {
            //phHueSDK.setSelectedBridge(b);
            //phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            //Here it is recommended to set your connected bridge in your sdk object (as above) and start the heartbeat.
            //At this point you are connected to a bridge so you should pass control to your main program/activity.
            //The username is generated randomly by the bridge.
            //Also, it is recommended to store the connected IP Address/Username in your app here.
            //This will allow easy automatic connection on subsequent use.
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
            phHueSDK.startPushlinkAuthentication(phAccessPoint);
            //Arriving here indicates that Pushlinking is required (to prove the User has physical access to the bridge).
            //Typically here you will display a pushlink image (with a timer) indication to the user they need to push the
            //button
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> list) {
            //Handle your bridge search results here.
            //Typically if multiple results are returned you will want to display them in a list and let the user select their bridge
            //If only one is found you may opt to connect automatically to that bridge.
        }

        @Override
        public void onError(int i, String s) {
            //Here you can handle events such as Bridge Not Responding, Authentication Failed, and Bridge Not Found.
        }

        @Override
        public void onConnectionResumed(PHBridge phBridge) {

        }

        @Override
        public void onConnectionLost(PHAccessPoint phAccessPoint) {
            //Here you would handle the loss of connection to your bridge.
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> list) {
            //Any JSON parsing errors are returned here. Typically your program should never return these.
        }
    };

    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
    //sm.search(true, true);

    PHAccessPoint accessPoint = new PHAccessPoint();
   // accessPoint.setIpAddress();     //put stored IP address here
   // accessPoint.setUsername();           //put stored username here
   // phHueSDK.connect(accessPoint);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_lights);
    }
}




