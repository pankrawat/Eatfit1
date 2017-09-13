package com.eatfit.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.network.NetworkThread;
import com.google.gson.JsonObject;

public class Rewards extends AppCompatActivity implements View.OnClickListener {
    private TextView updatesMessage, redeemPoints;
    private Dialog progressDialog;
    private SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        // TODO get back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO set custom navigation icon on toolbar
        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spMain = spMain.getInstance(this);

        updatesMessage = (TextView) findViewById(R.id.updatesMessage);
        redeemPoints = (TextView) findViewById(R.id.redeemPoints);

        updatesMessage.setText("You have " + spMain.getString(Constants.REWARDS, "") + " Rewards points.");

//        getRewardsPoints();


        redeemPoints.setOnClickListener(this);
    }

//    private void getRewardsPoints() {
//        try {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("versionCode", versionCode);
//            jsonObject.addProperty("versionNumber", versionNumber);
//
//            Log.e("checkVersion", jsonObject.toString());
//
//            if (Utils.isConnectingToInternet(this)) {
//                progressDialog = MyProgressDialog.showProgressDialog(this, "");
//                progressDialog.show();
//                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "checkVersion");
//                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
//            } else {
//                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redeemPoints:

                break;
        }
    }
}
