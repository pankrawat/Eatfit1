package com.eatfit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.Utils;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

public class Updates extends AppCompatActivity implements NetworkCallback {
    RelativeLayout updatesLay;
    private Dialog progressDialog;
    String versionNumber = "";
    String versionCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);

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

        updatesLay = (RelativeLayout) findViewById(R.id.updatesLay);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumber = pInfo.versionName;
            versionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getUpdates();
    }

    private void getUpdates() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("versionCode", versionCode);
            jsonObject.addProperty("versionNumber", versionNumber);

            Log.e("checkVersion", jsonObject.toString());

            if (Utils.isConnectingToInternet(this)) {
                progressDialog = MyProgressDialog.showProgressDialog(this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "checkVersion");
                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {

        if (this != null) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("checkVersion")) {
                        updatesLay.setVisibility(View.VISIBLE);
                    }
                } else {
//                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    updatesLay.setVisibility(View.VISIBLE);
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {

    }
}
