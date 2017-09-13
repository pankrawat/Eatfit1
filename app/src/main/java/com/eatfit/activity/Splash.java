package com.eatfit.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.SharedPreference;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    SharedPreference spMain;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        spMain = spMain.getInstance(this);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // This method will be executed once the timer is over
                // Start your app main activity
                if (spMain.getBoolean(Constants.ISProfile, false)) {
                    Intent in = new Intent(Splash.this, Home.class);
                    startActivity(in);
                    finish();
                } else if (spMain.getBoolean(Constants.ISLogin, false)) {
                    Intent in = new Intent(Splash.this, BingoScreen.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(Splash.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        };

        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }
}
