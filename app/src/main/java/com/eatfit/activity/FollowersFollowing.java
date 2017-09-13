package com.eatfit.activity;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.FeedAdapter;
import com.eatfit.adapter.NutritionDishesAdapter;
import com.eatfit.adapter.UserFollowersAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.FeedFragment;
import com.eatfit.fragments.Nutrition;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.FeedPostUserModel;
import com.eatfit.model.FollowersModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.eatfit.R.id.userPost;

public class FollowersFollowing extends AppCompatActivity implements View.OnClickListener, NetworkCallback {

    SharedPreference spMain;
    boolean category = false;
    TextView followers, following;
    LinearLayout followersBackgroundLay;
    Dialog progressDialog;
    private RecyclerView followersList;
    private ArrayList<FollowersModel> followersModelArrayList;
    UserFollowersAdapter userFollowersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followersfollowing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back);

        init();
        spMain = spMain.getInstance(this);
        followersModelArrayList = new ArrayList<>();

        getFollowersList();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
    }

    private void getFollowersList() {

        if (category) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));

                Log.e("followersList", jsonObject.toString());

                if (Utils.isConnectingToInternet(this)) {
                    progressDialog = MyProgressDialog.showProgressDialog((Activity) this, "");
                    progressDialog.show();
                    NetworkThread networkThread = new NetworkThread(this, Constants.URL + "getFolloweringUsers");
                    networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
                } else {
                    Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));

                Log.e("followingList", jsonObject.toString());

                if (Utils.isConnectingToInternet(this)) {
                    progressDialog = MyProgressDialog.showProgressDialog((Activity) this, "");
                    progressDialog.show();
                    NetworkThread networkThread = new NetworkThread(this, Constants.URL + "getfollowerUsers");
                    networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
                } else {
                    Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {

        followersBackgroundLay = (LinearLayout) findViewById(R.id.buttonfollowers);
        followers = (TextView) findViewById(R.id.followers);
        following = (TextView) findViewById(R.id.following);
        followersList = (RecyclerView) findViewById(R.id.followerList);

        followersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        followers.setOnClickListener(this);
        following.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.followers:
                category = false;
                followersBackgroundLay.setBackground(getResources().getDrawable(R.mipmap.veg));
                getFollowersList();
                break;

            case R.id.following:
                category = true;
                followersBackgroundLay.setBackground(getResources().getDrawable(R.mipmap.nonveg));
                getFollowersList();
                break;
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
                    if (fromUrl.contains("getfollowerUsers")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");

                        if (followersModelArrayList.size() >= 0)
                            followersModelArrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject res = jsonArray.getJSONObject(i);

                            FollowersModel followersModel = new FollowersModel();

                            followersModel.setName(res.getString("name"));
                            followersModel.setId(res.getString("id"));
                            followersModel.setFollowerId(res.getString("follower_id"));
                            followersModel.setFollowingId(res.getString("following_id"));
                            followersModel.setProfileImage(res.getString("profile_image"));

                            followersModelArrayList.add(followersModel);
                        }
                        userFollowersAdapter = new UserFollowersAdapter(this, followersModelArrayList);
                        followersList.setAdapter(userFollowersAdapter);

                    } else if (fromUrl.contains("getFolloweringUsers")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");

                        if (followersModelArrayList.size() >= 0)
                            followersModelArrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject res = jsonArray.getJSONObject(i);

                            FollowersModel followersModel = new FollowersModel();

                            followersModel.setName(res.getString("name"));
                            followersModel.setId(res.getString("id"));
                            followersModel.setFollowerId(res.getString("follower_id"));
                            followersModel.setFollowingId(res.getString("following_id"));
                            followersModel.setProfileImage(res.getString("profile_image"));

                            followersModelArrayList.add(followersModel);
                        }
                        userFollowersAdapter = new UserFollowersAdapter(this, followersModelArrayList);
                        followersList.setAdapter(userFollowersAdapter);
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
