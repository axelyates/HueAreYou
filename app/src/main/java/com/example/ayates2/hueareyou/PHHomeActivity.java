package com.example.ayates2.hueareyou;

import java.util.List;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;



public class PHHomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private PHHueSDK phHueSDK;
    public static final String TAG = "QuickStart";
    private HueSharedPreferences prefs;
    private AccessPointListAdapter adapter;

    private boolean lastSearchWasIPScan = false;

    @Override
    //onCreate is a method that creates a new instance
    //of a bridge connection or auto-runs a bridge search
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_lights);

        //gets an instance of the Hue SDK
        phHueSDK = PHHueSDK.create();

        //sets the name of app in white list
        phHueSDK.setAppName("HueAreYou");

        //sets the device in white list
        phHueSDK.setDeviceName(Build.MODEL);

        //registers the PHSDKListener to receive callbacks from bridge
        phHueSDK.getNotificationManager().registerSDKListener(listener);

        adapter = new AccessPointListAdapter(getApplicationContext(), phHueSDK.getAccessPointsFound());

        ListView accessPointList = (ListView) findViewById(R.id.bridge_list);
        accessPointList.setOnItemClickListener(this);
        accessPointList.setAdapter(adapter);

        //tries to automatically connect to last known bridge.
        //for first time use, this will be empty so it auto searches.
        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress = prefs.getLastConnectedIPAddress();
        String lastUsername = prefs.getUsername();

        //automatically trying to connect to last known IP address
        if(lastIpAddress != null && !lastIpAddress.equals("")){
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if(!phHueSDK.isAccessPointConnected(lastAccessPoint)){
                //a wizard pops up telling user what to do...i need to implement this
                PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, PHWizardAlertDialog.this);
                phHueSDK.connect(lastAccessPoint);
            }
        }
        else{
            //runs if this is a first time use, so perform a bridge search
            doBridgeSearch();
        }
    }
    @Override
    //this method creates an option menu.. >.> ..
    public boolean onCreateOptionsMenu(Menu menu){
        Log.w(TAG, "Inflating home menu");

        //inflate the menu, this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    //Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {
        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge bridge) {
            Log.w(TAG, "On CacheUpdated");
        }

        @Override
        public void onBridgeConnected(PHBridge b, String username) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);

            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());

            prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(username);

            PHWizardAlertDialog.getInstance().closeProgressDialog();
            startMainActivity();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(PHWizardAlertDialog.this, PHPushlinkActivity.class));
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found. " + accessPoint.size());

            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if(accessPoint != null && accessPoint.size() > 0){
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(phHueSDK.getAccessPointsFound())
                    }
                });
            }
        }

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);
            if(code == PHHueError.NO_CONNECTION){
                Log.w(TAG, "On No Connection");
            }
            else if(code == PHHueError.AUTHENTICATION_FAILED || code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED){
                PHWizardAlertDialog.getInstance().closeProgressDialog();
            }
            else if(code == PHHueError.BRIDGE_NOT_RESPONDING){
                Log.w(TAG, "Bridge Not Responding...");
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                PHWizardAlertDialog.this.runOnUiThread(new Runnable(){
                   @Override
                    public void run(){
                       PHWizardAlertDialog.showErrorDialog(PHWizardAlertDialog.this, message, R.string.btn_ok);
                   }
                });
            }
            else if(code == PHMessageType.BRIDGE_NOT_FOUND){
                if(!lastSearchWasIPScan){
                    //perform an IP scan
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan = true;
                }
                else{
                    PHWizardAlertDialog.getInstance().closeProgressDialog();
                    PhHomeActivity.this.runOnUiThread(new Runnable(){
                       @Override
                        public void run(){
                           PHWizardAlertDialog.showErrorDialog(PHWizardAlertDialog.this, message, R.string.btn_ok);
                       }
                    });
                }
            }
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if(PHWizardAlertDialog.this.isFinishing()){
                return;
            }
            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());

            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            for(int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++){
                if(phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())){
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if(!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)){
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for(PHHueParsingError parsingError: parsingErrorsList){
                Log.e(TAG, "ParsingError : " + parsingError.getMessage());
            }
        }
    };

    //called when "option" is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.find_new_bridge:
                doBridgeSearch();
                break;
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(listener != null){
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
        phHueSDK.disableAllHeartbeat();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);

        PHBridge connectedBridge = phHueSDK.getSelectedBridge();

        if(connectedBridge != null){
            String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            If(connectedIP != null){
                //we are already connected here
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
        }

        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, PHWizardAlertDialog.this);
        phHueSDK.connect(accessPoint);
    }

    public void doBridgeSearch(){
        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, PHWizardAlertDialog.this);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);

        //start the UPNP searching of local bridges
        sm.search(true, true);
    }

    //starting the activity this way, prevents the PushLink Activity being shown when pressing the back button
    public void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(), RandomColorButton.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            intent.addFlags(0x8000);
            //equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
            startActivity(intent);
        }
    }
}