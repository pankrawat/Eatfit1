package com.eatfit.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.MyNutritionAdapter;
import com.eatfit.adapter.NotificationAdpater;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealHistory;
import com.eatfit.model.Notification;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity implements NetworkCallback {
    RecyclerView notificationList;
    LinearLayoutManager linearLayoutManager;
    private Dialog progressDialog;
    private SharedPreference spMain;
    ArrayList<Notification> notificationArrayList;
    Notification notification;
    NotificationAdpater notificationAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        notificationArrayList = new ArrayList<>();
        notificationList = (RecyclerView) findViewById(R.id.notificationList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        notificationList.setLayoutManager(linearLayoutManager);

        getNotification();
    }


    private void getNotification() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
            Log.e("getMeal History", jsonObject.toString());
            if (Utils.isConnectingToInternet(this)) {
                progressDialog = MyProgressDialog.showProgressDialog(this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "userNotificationsDetail");
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
            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("userNotificationsDetail")) {
                        for (int i = 0; i < jsonObject.getJSONArray("Result").length(); i++) {
                            JSONObject res = jsonObject.getJSONArray("Result").getJSONObject(i);
                            notification = new Gson().fromJson(res.toString(), Notification.class);
                            notificationArrayList.add(notification);
                        }

                        notificationAdpater = new NotificationAdpater(this, notificationArrayList);
                        notificationList.setAdapter(notificationAdpater);
                    }
                } else {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {
        progressDialog.dismiss();
    }
}
