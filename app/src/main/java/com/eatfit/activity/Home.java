package com.eatfit.activity;

import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.adapter.MyNutritionAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.FeedFragment;
import com.eatfit.fragments.Nutrition;
import com.eatfit.fragments.ProfileFragment;
import com.eatfit.R;
import com.eatfit.fragments.Workout;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealHistory;
import com.eatfit.model.VideosModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Home extends AppCompatActivity implements View.OnClickListener, NetworkCallback {

    RelativeLayout tab_profile, tab_regimen, tab_nutrition, tab_workouts, tab_feeds;
    ImageView profile, regimen, workouts, nutrition, feeds, notify;
    TextView title;
    private String calorie = "";
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private RelativeLayout notificationLay;
    private SharedPreference spMain;
    public ArrayList<VideosModel> beginnersVideosArrayList, advancedVideosArrayList, intermediateVideosArrayList,
            beginnersExercisesArrayList, advancedExercisesArrayList, intermediateExercisesArrayList;
    private long backPressesd;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_space);
        if ((fragment instanceof ProfileFragment)) {
            if (backPressesd + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                backPressesd = System.currentTimeMillis();
                Toast.makeText(this, "Tap back once again in order to exit", Toast.LENGTH_SHORT).show();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_space, new ProfileFragment()).commit();
            title.setText(getResources().getString(R.string.profile));
            changeIconColor(1);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        // TODO get back button on toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO set custom navigation icon on toolbar
        toolbar.setNavigationIcon(null);

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
*/
        spMain = spMain.getInstance(this);

        Intent homeIntent = getIntent();
        calorie = homeIntent.getStringExtra("calorie");

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_space, new ProfileFragment()).commit();

        initViews();
        title.setText(getResources().getString(R.string.profile));

        getNotificationCount();

        beginnersVideosArrayList = new ArrayList<>();
        beginnersExercisesArrayList = new ArrayList<>();
        intermediateVideosArrayList = new ArrayList<>();
        intermediateExercisesArrayList = new ArrayList<>();
        advancedVideosArrayList = new ArrayList<>();
        advancedExercisesArrayList = new ArrayList<>();

        tab_workouts.setOnClickListener(this);
        tab_feeds.setOnClickListener(this);
//        tab_regimen.setOnClickListener(this);
        tab_nutrition.setOnClickListener(this);
        tab_profile.setOnClickListener(this);
        notificationLay.setOnClickListener(this);
    }

    private void getNotificationCount() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
            Log.e("NotificationCount", jsonObject.toString());
            if (Utils.isConnectingToInternet(this)) {
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "userNotificationsCount");
                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        tab_profile = (RelativeLayout) findViewById(R.id.tab_profile);
        tab_nutrition = (RelativeLayout) findViewById(R.id.tab_nutrition);
//        tab_regimen = (RelativeLayout) findViewById(R.id.tab_regimen);
        tab_feeds = (RelativeLayout) findViewById(R.id.tab_feeds);
        tab_workouts = (RelativeLayout) findViewById(R.id.tab_workout);

        profile = (ImageView) findViewById(R.id.image_profile);
        feeds = (ImageView) findViewById(R.id.image_feeds);
        nutrition = (ImageView) findViewById(R.id.image_nutrition);
        workouts = (ImageView) findViewById(R.id.image_workout);
//        regimen = (ImageView) findViewById(R.id.image_regimen);

        notificationLay = (RelativeLayout) findViewById(R.id.notificationLay);
        notify = (ImageView) findViewById(R.id.notify);
        title = (TextView) findViewById(R.id.app_name);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tab_profile:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_space, new ProfileFragment()).commit();
                title.setText(getResources().getString(R.string.profile));
                changeIconColor(1);
                break;

            case R.id.tab_nutrition:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_space, new Nutrition()).commit();
                title.setText(getResources().getString(R.string.nutrition));
                changeIconColor(2);
                break;

            case R.id.tab_workout:
                if (Utils.isConnectingToInternet(this)) {
                    getVideoList();
                } else {
                    Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tab_feeds:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_space, new FeedFragment()).commit();
                title.setText(getResources().getString(R.string.feed));
                changeIconColor(4);
                break;

            case R.id.notificationLay:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
        }

    }

    public void changeIconColor(int number) {

        switch (number) {
            case 1:
                profile.setImageDrawable(getResources().getDrawable(R.mipmap.profile_green));
                nutrition.setImageDrawable(getResources().getDrawable(R.mipmap.nutrition));
                feeds.setImageDrawable(getResources().getDrawable(R.mipmap.feed));
                workouts.setImageDrawable(getResources().getDrawable(R.mipmap.workout));
                break;

            case 2:
                profile.setImageDrawable(getResources().getDrawable(R.mipmap.profile));
                nutrition.setImageDrawable(getResources().getDrawable(R.mipmap.nutrition_green));
                feeds.setImageDrawable(getResources().getDrawable(R.mipmap.feed));
                workouts.setImageDrawable(getResources().getDrawable(R.mipmap.workout));
                break;

            case 3:
                profile.setImageDrawable(getResources().getDrawable(R.mipmap.profile));
                nutrition.setImageDrawable(getResources().getDrawable(R.mipmap.nutrition));
                feeds.setImageDrawable(getResources().getDrawable(R.mipmap.feed));
                workouts.setImageDrawable(getResources().getDrawable(R.mipmap.workout_green));
                break;

            case 4:
                profile.setImageDrawable(getResources().getDrawable(R.mipmap.profile));
                nutrition.setImageDrawable(getResources().getDrawable(R.mipmap.nutrition));
                feeds.setImageDrawable(getResources().getDrawable(R.mipmap.feed_green));
                workouts.setImageDrawable(getResources().getDrawable(R.mipmap.workout));
                break;
        }

    }

    public void getVideoList() {
        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));

        Log.e("VideoApi Hit", jsonObject.toString());

        Ion.with(this)
                .load(Constants.URL + "videosList")
                .setJsonObjectBody(jsonObject)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        dialog.dismiss();
                        try {
                            if (e == null && result != null) {
                                JSONObject results = new JSONObject(result);
                                Log.e("Result Videos", result.toString());

                                if (results.optBoolean("isSuccess")) {
                                    JSONObject res = results.getJSONObject("Result");
                                    JSONArray beginners = res.getJSONArray("beginners");

                                    if (beginnersVideosArrayList.size() > 0) {
                                        beginnersVideosArrayList.clear();
                                        beginnersExercisesArrayList.clear();
                                        intermediateVideosArrayList.clear();
                                        intermediateExercisesArrayList.clear();
                                        advancedVideosArrayList.clear();
                                        advancedExercisesArrayList.clear();
                                    }

                                    for (int i = 0; i < beginners.length(); i++) {
                                        JSONObject beginnersvideos = beginners.getJSONObject(i);

                                        if (beginnersvideos.getString("type").equals("0")) {
                                            VideosModel beginnersVideosModel = new VideosModel();
                                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                                            beginnersVideosModel.setLikeVideo(beginnersvideos.getInt("likeVideo"));
                                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                                            beginnersVideosArrayList.add(beginnersVideosModel);
                                        } else {

                                            VideosModel beginnersVideosModel = new VideosModel();
                                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                                            beginnersVideosModel.setLikeVideo(beginnersvideos.getInt("likeVideo"));
                                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                                            beginnersExercisesArrayList.add(beginnersVideosModel);
                                        }
                                    }
                                    JSONArray intermediate = res.getJSONArray("intermediate");

                                    for (int i = 0; i < intermediate.length(); i++) {
                                        JSONObject beginnersvideos = intermediate.getJSONObject(i);


                                        if (beginnersvideos.getString("type").equals("0")) {
                                            VideosModel beginnersVideosModel = new VideosModel();
                                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                                            beginnersVideosModel.setLikeVideo(beginnersvideos.getInt("likeVideo"));
                                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                                            intermediateVideosArrayList.add(beginnersVideosModel);
                                        } else {

                                            VideosModel beginnersVideosModel = new VideosModel();
                                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                                            beginnersVideosModel.setLikeVideo(beginnersvideos.getInt("likeVideo"));
                                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                                            intermediateExercisesArrayList.add(beginnersVideosModel);
                                        }
                                    }
                                    JSONArray advanced = res.getJSONArray("advanced");

                                    for (int i = 0; i < advanced.length(); i++) {
                                        JSONObject beginnersvideos = advanced.getJSONObject(i);


                                        if (beginnersvideos.getString("type").equals("0")) {
                                            VideosModel beginnersVideosModel = new VideosModel();
                                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                                            beginnersVideosModel.setLikeVideo(beginnersvideos.getInt("likeVideo"));
                                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                                            advancedVideosArrayList.add(beginnersVideosModel);
                                        } else {

                                            VideosModel beginnersVideosModel = new VideosModel();
                                            beginnersVideosModel.setId(beginnersvideos.getString("id"));
                                            beginnersVideosModel.setName(beginnersvideos.getString("name"));
                                            beginnersVideosModel.setType(beginnersvideos.getString("type"));
                                            beginnersVideosModel.setDescription(beginnersvideos.getString("description"));
                                            beginnersVideosModel.setLink(beginnersvideos.getString("link"));
                                            beginnersVideosModel.setLikeVideo(beginnersvideos.getInt("likeVideo"));
                                            beginnersVideosModel.setIsAlreadyAddedInWorkout(beginnersvideos.getInt("IsAlreadyAddedInWorkout"));

                                            advancedExercisesArrayList.add(beginnersVideosModel);
                                        }
                                    }
                                }
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_space, new Workout()).commit();
                                title.setText(getString(R.string.workouts));
                                changeIconColor(3);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {
        if (this != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("userNotificationsCount")) {
                        if (Integer.parseInt(jsonObject.getString("Result")) > 0) {
                            notify.setVisibility(View.VISIBLE);
                        } else {
                            notify.setVisibility(View.GONE);
                        }
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

    }
}
