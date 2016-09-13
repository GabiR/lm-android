package com.cypien.leroy.activities;/*
 * Created by Alex on 05.09.2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cypien.leroy.R;

public class SplashScreenActivity extends AppCompatActivity {
    private SharedPreferences.Editor spEditor;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
       /*sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);
        if ( sp.getBoolean("isConnected", false) && Connections.isNetworkConnected(getApplicationContext())) {
            login(sp.getString("username", ""), sp.getString("password", ""));
        }*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }



}
