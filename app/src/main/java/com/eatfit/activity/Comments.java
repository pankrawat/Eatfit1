package com.eatfit.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.CommentsAdapter;
import com.eatfit.adapter.FeedAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.CommentsModel;
import com.eatfit.model.FeedPostUserModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Comments extends AppCompatActivity implements NetworkCallback, View.OnClickListener {

    RecyclerView commentsList;
    SharedPreference spMain;
    CommentsAdapter commentsAdapter;
    String postId = "";
    Dialog progressDialog;
    ArrayList<CommentsModel> commentsArrayList;
    ImageView send;
    EditText postadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back_black);

        spMain = spMain.getInstance(this);
        commentsArrayList = new ArrayList<>();


        commentsList = (RecyclerView) findViewById(R.id.commentsList);
        send = (ImageView) findViewById(R.id.send);
        postadd = (EditText) findViewById(R.id.addcomment);

        postId = getIntent().getStringExtra("post_id");

        getPostComments();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        commentsList.setLayoutManager(linearLayoutManager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        send.setOnClickListener(this);
    }

    private void getPostComments() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("postId", postId);

            Log.e("commentPost", jsonObject.toString());
            if (Utils.isConnectingToInternet(this)) {

                progressDialog = MyProgressDialog.showProgressDialog(this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "getComments");
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
                    if (fromUrl.contains("getComments")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");

                        if (commentsArrayList.size() >= 0)
                            commentsArrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject res = jsonArray.getJSONObject(i);

                            CommentsModel commentsModel = new CommentsModel();

                            commentsModel.setFirstName(res.getString("first_name"));
                            commentsModel.setLastName(res.getString("last_name"));
                            commentsModel.setId(res.getString("id"));
                            commentsModel.setPostId(res.getString("postId"));
                            commentsModel.setComment(res.getString("comment"));
                            commentsModel.setProfileImage(res.getString("profile_image"));
                            commentsModel.setUserId(res.getString("userId"));

                            commentsArrayList.add(commentsModel);
                        }

                        if (commentsAdapter == null) {
                            commentsAdapter = new CommentsAdapter(this, commentsArrayList);
                            commentsList.setAdapter(commentsAdapter);
                        } else {
                            commentsAdapter.notifyDataSetChanged();
                        }


                    } else if (fromUrl.contains("commentPost")) {
                        postadd.setText("");
                        getPostComments();
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
        Toast.makeText(this, Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.send:
                if (postadd.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Please enter comment to post", Toast.LENGTH_SHORT).show();
                } else {
                    addComments();
                }

                break;
        }
    }

    private void addComments() {

        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
            jsonObject.addProperty("comment", postadd.getText().toString().trim());
            jsonObject.addProperty("postId", postId);

            Log.e("comment add Post", jsonObject.toString());
            if (Utils.isConnectingToInternet(this)) {

                progressDialog = MyProgressDialog.showProgressDialog(this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "commentPost");
                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
