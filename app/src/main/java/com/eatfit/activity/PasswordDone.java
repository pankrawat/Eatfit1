package com.eatfit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.helper.SharedPreference;

public class PasswordDone extends AppCompatActivity {
    TextView loginButton;
    private SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passworddone);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(null);
//        // TODO get back button on toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        // TODO set custom navigation icon on toolbar
//        toolbar.setNavigationIcon(R.mipmap.back_black);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        initViews();
        spMain = spMain.getInstance(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spMain.deletePreference();
                startActivity(new Intent(PasswordDone.this, LoginActivity.class));
                finishAffinity();
            }
        });
    }

    private void initViews() {

        loginButton = (TextView) findViewById(R.id.loginButton);
    }
}
