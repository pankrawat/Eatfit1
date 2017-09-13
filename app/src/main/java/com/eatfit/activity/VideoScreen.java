package com.eatfit.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.eatfit.R;
import com.eatfit.adapter.RemainingVideoListAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.VideosModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoScreen extends YouTubeBaseActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener, NetworkCallback {
    public static final String API_KEY = "AIzaSyBYJwLbcVlc5gCkrYaGJ-vb0KNEnoIhgtU";
    public String VIDEO_ID = "";
    private String fragmentTitle;
    MediaController mediaController;
    VideoView videoView;
    Uri video;
    private RecyclerView videoList;
    private RelativeLayout like, share, add;
    private TextView channelTitle, channelSubTitle, videoTitle, videoSubTitle,
            likes, shareText, addText, descriptionChannel, app_name;
    private ImageView channelImage, videoImage, expandImage, likeImage, addImage, shareImage, backImage;
    private SharedPreference spMain;
    public ArrayList<VideosModel> videosModelArrayList, demoModelArrayList;
    private int pos;
    private YouTubePlayerView youTubePlayerView;
    private Dialog progressDialog;
    public VideosModel videosModel, nextDemoModel;
    private RemainingVideoListAdapter remainingVideoListAdapter;
    private boolean _isFullScreen = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (!_isFullScreen) {

            descriptionChannel.setVisibility(View.GONE);
            descriptionChannel.setText(videosModel.getDescription());

            videosModelArrayList.remove(pos);

            VIDEO_ID = Utils.getYoutubeVideoId(videosModel.getLink());
            Log.e("ID_Video", VIDEO_ID);


            youTubePlayerView.initialize(API_KEY, this);

            remainingVideoListAdapter = new RemainingVideoListAdapter(this, videosModelArrayList, fragmentTitle);
            videoList.setAdapter(remainingVideoListAdapter);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoscreen);

        getIntentData();
        spMain = spMain.getInstance(this);
        init();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        pos = Integer.parseInt(intent.getStringExtra("pos"));
        videosModelArrayList = (ArrayList<VideosModel>) intent.getSerializableExtra("listData");
        demoModelArrayList = (ArrayList<VideosModel>) intent.getSerializableExtra("listData");
        videosModel = (VideosModel) intent.getSerializableExtra("currentListItem");
        fragmentTitle = intent.getStringExtra("string");
    }

    private void init() {

        videoList = (RecyclerView) findViewById(R.id.nextVideoList);
        like = (RelativeLayout) findViewById(R.id.likePanel);
        share = (RelativeLayout) findViewById(R.id.sharePanel);
        add = (RelativeLayout) findViewById(R.id.addPanel);
        channelTitle = (TextView) findViewById(R.id.titleChanel);
        channelSubTitle = (TextView) findViewById(R.id.subtitleChanel);
        videoTitle = (TextView) findViewById(R.id.titleVideo);
        videoSubTitle = (TextView) findViewById(R.id.subTitleVideo);
        likes = (TextView) findViewById(R.id.likeVideo);
        shareText = (TextView) findViewById(R.id.shareVideo);
        addText = (TextView) findViewById(R.id.addVideo);
        channelImage = (ImageView) findViewById(R.id.channelImage);
        expandImage = (ImageView) findViewById(R.id.expandImage);
        descriptionChannel = (TextView) findViewById(R.id.descriptionChanel);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        likeImage = (ImageView) findViewById(R.id.likeIcon);
        addImage = (ImageView) findViewById(R.id.addIcon);
        backImage = (ImageView) findViewById(R.id.imgBack);
        app_name = (TextView) findViewById(R.id.app_name);

        videoList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getVideoDescription();

        like.setOnClickListener(this);
        share.setOnClickListener(this);
        add.setOnClickListener(this);
        expandImage.setOnClickListener(this);
        backImage.setOnClickListener(this);
    }

    private void getVideoDescription() {

        if (videosModel.getLikeVideo() == 1) {
            likeImage.setImageResource(R.mipmap.like_white);
        } else {
            likeImage.setImageResource(R.mipmap.liken);
        }

        if (videosModel.getIsAlreadyAddedInWorkout() == 1) {
            addImage.setImageResource(R.mipmap.plus_white);
        }

        if (fragmentTitle.equals(Constants.Beginners)) {
            app_name.setText("Beginners Video");
        } else if (fragmentTitle.equals(Constants.Intermediate)) {
            app_name.setText("Intermediate Video");
        } else if (fragmentTitle.equals(Constants.Advanced)) {
            app_name.setText("Advanced Video");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.expandImage:
                if (descriptionChannel.getVisibility() == View.GONE) {
                    descriptionChannel.setVisibility(View.VISIBLE);
                } else {
                    descriptionChannel.setVisibility(View.GONE);
                }
                break;

            case R.id.addPanel:

                if (videosModel.getIsAlreadyAddedInWorkout() == 1) {
                    Toast.makeText(this, "Video is already added into your workout", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
                        jsonObject.addProperty("videoId", videosModel.getId());

                        Log.e("workout", jsonObject.toString());

                        if (Utils.isConnectingToInternet(this)) {
                            progressDialog = MyProgressDialog.showProgressDialog(this, "");
                            progressDialog.show();
                            NetworkThread networkThread = new NetworkThread(this, Constants.URL + "addToWorkOut");
                            networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
                        } else {
                            Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;

            case R.id.likePanel:

                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
                    jsonObject.addProperty("videoId", videosModel.getId());

                    Log.e("likedVideo", jsonObject.toString());

                    if (Utils.isConnectingToInternet(this)) {
                        progressDialog = MyProgressDialog.showProgressDialog(this, "");
                        progressDialog.show();
                        NetworkThread networkThread = new NetworkThread(this, Constants.URL + "likeVideo");
                        networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.imgBack:
                finish();
                break;

            case R.id.sharePanel:
                sharePopup();
                break;
        }

    }

    /*public void changetheListVideo(int position) {
        VIDEO_ID = Utils.getYoutubeVideoId(videosModelArrayList.get(position).getLink());
        Log.e("ID_Video", VIDEO_ID);

        nextDemoModel = videosModelArrayList.get(position);
        videosModelArrayList.remove(position);
        videosModelArrayList.add(videosModel);
        videosModel = nextDemoModel;

        getVideoDescription();

        remainingVideoListAdapter.notifyDataSetChanged();
    }*/

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;

        // Start buffering
        if (!wasRestored) {
            player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean b) {
                    _isFullScreen = b;
                    Log.e("fullscreen detect", String.valueOf(_isFullScreen));
                }
            });
            player.cueVideo(VIDEO_ID);
        }

        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onAdStarted() {
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason arg0) {
            }

            @Override
            public void onLoaded(String arg0) {
            }

            @Override
            public void onLoading() {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onVideoStarted() {
            }
        });


        player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onBuffering(boolean arg0) {
            }

            @Override
            public void onPaused() {
            }

            @Override
            public void onPlaying() {
            }

            @Override
            public void onSeekTo(int arg0) {
            }

            @Override
            public void onStopped() {
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {
        if (this != null) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains(Constants.ADD_TO_WORKOUT)) {

                        videosModel.setIsAlreadyAddedInWorkout(1);
                        addImage.setImageResource(R.mipmap.plus_white);
                        Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } else if (fromUrl.contains(Constants.LIKE_VIDEO)) {

                        if (!jsonObject.getString("message").contains("UnLiked")) {
                            likeImage.setImageResource(R.mipmap.like_white);
                            videosModel.setLikeVideo(1);
                        } else {
                            likeImage.setImageResource(R.mipmap.liken);
                            videosModel.setLikeVideo(0);
                        }

                        Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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

    private void sharePopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.share_popup);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        RelativeLayout shareInside = (RelativeLayout) dialog.findViewById(R.id.shareInside);
        RelativeLayout shareOutsideApp = (RelativeLayout) dialog.findViewById(R.id.shareOutsideApp);

        shareInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoScreen.this, "It will implement with Feed Screen ", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        shareOutsideApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(videosModelArrayList.get(pos).getLink());
                Log.e("url String", uri.toString());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, videosModelArrayList.get(pos).getName() + "\\n"
                        + videosModelArrayList.get(pos).getDescription());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
