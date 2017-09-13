package com.eatfit.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.RemainingVideoListAdapter;
import com.eatfit.adapter.UserFollowersAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.FollowersModel;
import com.eatfit.model.VideosModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyWorkouts extends AppCompatActivity implements NetworkCallback {

    private SharedPreference spMain;
    RecyclerView exerciseList;
    private Dialog progressDialog;
    private ArrayList<VideosModel> videosModelArrayList;
    private RemainingVideoListAdapter remainingVideoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workouts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back);

        spMain = spMain.getInstance(this);
        initViews();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isConnectingToInternet(this)) {
            getUservideoList();
        } else {
            Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {


        videosModelArrayList = new ArrayList<>();
        exerciseList = (RecyclerView) findViewById(R.id.exerciseList);

        exerciseList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void getUservideoList() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));

            Log.e("followersList", jsonObject.toString());

            if (Utils.isConnectingToInternet(this)) {
                progressDialog = MyProgressDialog.showProgressDialog((Activity) this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "userWorkoutList");
                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

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
                    if (fromUrl.contains("userWorkoutList")) {

                        if (videosModelArrayList.size() > 0)
                            videosModelArrayList.clear();

                        JSONArray res = jsonObject.getJSONArray("Result");
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject beginnersvideos = res.getJSONObject(i);
                            VideosModel beginnersVideosModel = new VideosModel();
                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                            videosModelArrayList.add(beginnersVideosModel);
                        }

                        remainingVideoListAdapter = new RemainingVideoListAdapter(this, videosModelArrayList, "not");
                        exerciseList.setAdapter(remainingVideoListAdapter);

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {

    }
}
