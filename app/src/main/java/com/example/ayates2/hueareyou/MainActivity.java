package com.example.ayates2.hueareyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button lets_go_button;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        OnClickButtonListener();
    }

    public void OnClickButtonListener(){
        lets_go_button = (Button)findViewById(R.id.lets_get_started_button);
        lets_go_button.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //put logic (here?) where if button is clicked and no hue bridge is known,
                        //it goes to connect lights, but if there is already a bridge, then app
                        //goes directly to modifying lights.
                        Intent i = new Intent("com.example.ayates2.hueareyou.ConnectLights");
                        startActivity(i);
                    }
                }
        );
    }


}