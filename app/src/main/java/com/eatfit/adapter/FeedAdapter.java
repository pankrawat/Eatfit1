package com.eatfit.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
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
import com.eatfit.activity.Comments;
import com.eatfit.activity.EditProfile;
import com.eatfit.activity.Home;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.FeedFragment;
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
 * Created by appsquadz on 7/7/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.UserFeedPostHolder> implements View.OnClickListener, NetworkCallback {

    Context context;
    ArrayList<FeedPostUserModel> feedPostUserModelArrayList;
    SharedPreference spMain;
    Dialog progressDialogl;
    private FeedFragment feedFragment;
    int currentPosition;

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class VIEW_TYPES {
        public static final int Normal = 0;
        public static final int Footer = 1;
    }

    public FeedAdapter(Context context, ArrayList<FeedPostUserModel> feedArrayList, FeedFragment feedFragment) {
        this.feedPostUserModelArrayList = feedArrayList;
        this.context = context;
        this.feedFragment = feedFragment;
        spMain = spMain.getInstance(context);
    }

    @Override
    public UserFeedPostHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        /*switch (viewType) {
            case VIEW_TYPES.Normal:

                break;
            case VIEW_TYPES.Footer:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedpost_item, parent, false);
                break;
        }*/
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new UserFeedPostHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final UserFeedPostHolder holder, int position) {
        int pos = position;
        holder.postTexts.setText(feedPostUserModelArrayList.get(pos).getText());
        holder.userName.setText(feedPostUserModelArrayList.get(pos).getFirstName()
                + " " + feedPostUserModelArrayList.get(pos).getLastName());

        if (feedPostUserModelArrayList.get(pos).getIsLiked() == 1) {
            holder.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.like_active, 0, 0, 0);
        } else {
            holder.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.like, 0, 0, 0);
        }

        if (!spMain.getString(Constants.userID, "").equals(feedPostUserModelArrayList.get(pos).getUserId())) {
            if (holder.follow.getVisibility() == View.GONE)
                holder.follow.setVisibility(View.VISIBLE);
            if (feedPostUserModelArrayList.get(pos).getIsFollow() == 1) {
                holder.follow.setText(context.getResources().getString(R.string.follows));
            } else {
                holder.follow.setText(context.getResources().getString(R.string.follow));
            }
        } else {
            holder.follow.setVisibility(View.GONE);
        }

        Ion.with(context)
                .load(feedPostUserModelArrayList.get(pos).getProfileImage())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (e == null) {
                            holder.userImage.setImageBitmap(result);
                        } else {
                            holder.userImage.setImageResource(R.mipmap.profile_blue);
                        }
                    }
                });

        holder.likes.setOnClickListener(this);
        holder.likes.setTag(pos);
        holder.comment.setOnClickListener(this);
        holder.comment.setTag(pos);
        holder.follow.setOnClickListener(this);
        holder.follow.setTag(pos);

    }

    /* @Override
     public int getItemViewType(int position) {
         if (position == feedPostUserModelArrayList.size()) {
             return VIEW_TYPES.Footer;
         }
         return VIEW_TYPES.Normal;
     }
 */
    @Override
    public void onClick(final View v) {

        switch (v.getId()) {

            case R.id.commentPost:
                Intent in = new Intent(context, Comments.class);
                in.putExtra("post_id", feedPostUserModelArrayList.get(Integer.parseInt(v.getTag().toString())).getId());
                context.startActivity(in);

                break;

            case R.id.followerBtn:

                if (feedPostUserModelArrayList.get(Integer.parseInt(v.getTag().toString())).getIsFollow() == 1) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to unfollow ?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("follower_id", spMain.getString(Constants.userID, ""));
                                jsonObject.addProperty("following_id", feedPostUserModelArrayList.get(Integer.parseInt(v.getTag().toString())).getUserId());

                                Log.e("followUser", jsonObject.toString());
                                currentPosition = Integer.parseInt(v.getTag().toString());
                                if (Utils.isConnectingToInternet(context)) {
                                    NetworkThread networkThread = new NetworkThread(FeedAdapter.this, Constants.URL + "followUser");
                                    networkThread.getNetworkResponse(context, jsonObject, Constants.SHORT_TIME);
                                } else {
                                    Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                } else {
                    try {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("follower_id", spMain.getString(Constants.userID, ""));
                        jsonObject.addProperty("following_id", feedPostUserModelArrayList.get(Integer.parseInt(v.getTag().toString())).getUserId());

                        Log.e("followUser", jsonObject.toString());
                        currentPosition = Integer.parseInt(v.getTag().toString());
                        if (Utils.isConnectingToInternet(context)) {
                            NetworkThread networkThread = new NetworkThread(this, Constants.URL + "followUser");
                            networkThread.getNetworkResponse(context, jsonObject, Constants.SHORT_TIME);
                        } else {
                            Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.likePost:
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
                    jsonObject.addProperty("postId", feedPostUserModelArrayList.get(Integer.parseInt(v.getTag().toString())).getId());

                    Log.e("likedVideo", jsonObject.toString());
                    currentPosition = Integer.parseInt(v.getTag().toString());
                    if (Utils.isConnectingToInternet(context)) {

                        NetworkThread networkThread = new NetworkThread(this, Constants.URL + "likePost");
                        networkThread.getNetworkResponse(context, jsonObject, Constants.SHORT_TIME);
                    } else {
                        Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {
        if (context != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("likePost")) {
                        if (jsonObject.optString("message").contains("UnLiked")) {
                            feedPostUserModelArrayList.get(currentPosition).setIsLiked(0);
                        } else {
                            feedPostUserModelArrayList.get(currentPosition).setIsLiked(1);
                        }
                        notifyDataSetChanged();
                    } else if (fromUrl.contains("followUser")) {
                        if (jsonObject.optString("message").contains("UnFollow")) {
                            feedPostUserModelArrayList.get(currentPosition).setIsFollow(0);
                            feedFragment.getUserPostFeed();
                        } else {
                            feedPostUserModelArrayList.get(currentPosition).setIsFollow(1);
                            feedFragment.getUserPostFeed();
                        }
                        notifyDataSetChanged();
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
    public int getItemCount() {
        return feedPostUserModelArrayList.size();
    }

    public class UserFeedPostHolder extends RecyclerView.ViewHolder {

        private TextView likes, comment, follow, userName, postTexts, postMessage;
        private EditText userPost;
        private CircleImageView userImage, userPostImage;

        public UserFeedPostHolder(View itemView, int viewType) {
            super(itemView);

            likes = (TextView) itemView.findViewById(R.id.likePost);
            comment = (TextView) itemView.findViewById(R.id.commentPost);
            follow = (TextView) itemView.findViewById(R.id.followerBtn);
            userImage = (CircleImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            postTexts = (TextView) itemView.findViewById(R.id.postText);

        }
    }
}
