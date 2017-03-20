package com.example.ayates2.hueareyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ConnectLights extends AppCompatActivity{

    Button connect_lights_btn;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_connect_lights);
        OnClickButtonListener();
    }

    public void OnClickButtonListener(){
        connect_lights_btn = (Button)findViewById(R.id.connect_lights_button);
        connect_lights_btn.setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    /*
                     * this needs to connect to the service where the app
                     * talks to the bridge and connects to the lights.
                     * put logic here where "if there is no known bridge, it makes user
                     * activate pushlink action and connect bridge, but if there is one, go straight
                     * to the lights to control them.
                     */
                    Intent i = new Intent("com.example.ayates2.hueareyou.PHPushlinkActivity");
                    startActivity(i);
                }
            }
        );
    }
}