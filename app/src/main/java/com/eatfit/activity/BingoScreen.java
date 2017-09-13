package com.eatfit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class BingoScreen extends AppCompatActivity implements View.OnClickListener {

    TextView calculateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingoscreen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
//        // TODO get back button on toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        // TODO set custom navigation icon on toolbar
//        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initViews();

        calculateNow.setOnClickListener(this);
    }

    private void initViews() {
        calculateNow = (TextView) findViewById(R.id.calculate);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.calculate:
                if (Utils.isConnectingToInternet(this)) {
                    Intent personalInfo = new Intent(BingoScreen.this, PersonalInformation.class);
                    personalInfo.putExtra("navigater" , false);
                    startActivity(personalInfo);
                } else {
                    Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
