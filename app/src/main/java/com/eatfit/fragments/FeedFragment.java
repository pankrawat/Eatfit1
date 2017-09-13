package com.eatfit.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.FeedAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.CircleImageView;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.FeedPostUserModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements NetworkCallback, View.OnClickListener {


    private Context context;
    private Dialog progressDialog;
    private SharedPreference spMain;
    private RecyclerView feedUser;
    private ArrayList<FeedPostUserModel> feedPostUserModelArrayList;
    FeedAdapter feedAdapter;
    private TextView postMessage;
    private EditText userPost;
    private CircleImageView userPostImage;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        context = getActivity();
        spMain = spMain.getInstance(context);

        init(view);
        feedPostUserModelArrayList = new ArrayList<>();
        getUserPostFeed();


        return view;
    }

    private void init(View view) {
        feedUser = (RecyclerView) view.findViewById(R.id.feedList);

        userPostImage = (CircleImageView) view.findViewById(R.id.userPostImage);
        userPost = (EditText) view.findViewById(R.id.userPost);
        postMessage = (TextView) view.findViewById(R.id.postBtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        feedUser.setLayoutManager(linearLayoutManager);

        if (!spMain.getString(Constants.profileURL, "").equals("")) {
            Ion.with(this).load(spMain.getString(Constants.profileURL, ""))
                    .asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (e == null) {
                        userPostImage.setImageBitmap(result);
                    } else {
                        userPostImage.setImageResource(R.mipmap.profile_blue);
                    }
                }
            });
        }

        postMessage.setOnClickListener(this);
    }

    public void getUserPostFeed() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));

            Log.e("Post Json", jsonObject.toString());

            if (Utils.isConnectingToInternet(context)) {
                progressDialog = MyProgressDialog.showProgressDialog((Activity) context, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(FeedFragment.this, Constants.URL + "getAllPost");
                networkThread.getNetworkResponse(context, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {
        if (context != null) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("getAllPost")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");

                        if (feedPostUserModelArrayList.size() >= 0)
                            feedPostUserModelArrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject res = jsonArray.getJSONObject(i);

                            FeedPostUserModel feedPostUserModel = new FeedPostUserModel();

                            feedPostUserModel.setFirstName(res.getString("first_name"));
                            feedPostUserModel.setLastName(res.getString("last_name"));
                            feedPostUserModel.setId(res.getString("id"));
                            feedPostUserModel.setIsFollow(res.getInt("isFollow"));
                            feedPostUserModel.setIsLiked(res.getInt("isLiked"));
                            feedPostUserModel.setText(res.getString("text"));
                            feedPostUserModel.setProfileImage(res.getString("profile_image"));
                            feedPostUserModel.setUserId(res.getString("userId"));

                            feedPostUserModelArrayList.add(feedPostUserModel);
                        }
                        feedAdapter = new FeedAdapter(context, feedPostUserModelArrayList, this);
                        feedUser.setAdapter(feedAdapter);

                    } else if (fromUrl.contains("addPost")) {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        userPost.setText("");
                        getUserPostFeed();
                    }
                } else {
                    Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.postBtn:
                if (userPost.getText().toString().trim().equals("")) {
                    Toast.makeText(context, "Please enter some text to share", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
                        jsonObject.addProperty("text", userPost.getText().toString().trim());

                        Log.e("postUser", jsonObject.toString());
//                            currentPosition = Integer.parseInt(v.getTag().toString());
                        if (Utils.isConnectingToInternet(context)) {

                            NetworkThread networkThread = new NetworkThread(this, Constants.URL + "addPost");
                            networkThread.getNetworkResponse(context, jsonObject, Constants.SHORT_TIME);
                        } else {
                            Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
}
