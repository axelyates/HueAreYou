package com.example.ayates2.hueareyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button lets_go_button;

    public void OnClickButtonListener(){
        lets_go_button = (Button)findViewById(R.id.lets_get_started_button);
        lets_go_button.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent i = new Intent("com.example.ayates2.hueareyou.PHWizardAlertDialog");
                        startActivity(i);
                    }
                }
        );
    }


}