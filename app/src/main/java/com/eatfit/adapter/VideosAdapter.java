package com.eatfit.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.activity.VideoScreen;
import com.eatfit.fragments.Beginners;
import com.eatfit.helper.Utils;
import com.eatfit.model.VideosModel;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by appsquadz on 14/6/17.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosHolder> implements View.OnClickListener {
    private ArrayList<VideosModel> videosModelArrayList;
    private Context ctx;
    public String VIDEO_ID = "";
    private String stringFrag = "";

    public VideosAdapter(Context context, ArrayList<VideosModel> videosModelArrayList, String stringFragment) {
        this.ctx = context;
        this.stringFrag = stringFragment;
        this.videosModelArrayList = videosModelArrayList;
    }

    @Override
    public VideosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_item, parent, false);
        return new VideosHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideosHolder holder, int position) {
        holder.videoTitle.setText(videosModelArrayList.get(position).getName());
//        http://img.youtube.com/vi/<insert-youtube-video-id-here>/hqdefault.jpg

        String thumbURL = String.format("http://img.youtube.com/vi/%s/hqdefault.jpg",
                Utils.getYoutubeVideoId(videosModelArrayList.get(position).getLink()));
        Ion.with(ctx)
                .load(thumbURL)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            holder.bgImage.setImageBitmap(result);
                        }
                    }
                });

        holder.play.setOnClickListener(this);
        holder.videoItemParent.setOnClickListener(this);
        holder.play.setTag(position);
        holder.videoItemParent.setTag(position);
    }


    @Override
    public int getItemCount() {
        return videosModelArrayList.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.play:
                Intent nextVideo = new Intent(ctx, VideoScreen.class);
                nextVideo.putExtra("listData", videosModelArrayList);
                nextVideo.putExtra("currentListItem", videosModelArrayList.get((Integer) v.getTag()));
                nextVideo.putExtra("pos", v.getTag().toString());
                nextVideo.putExtra("string", stringFrag);
                ctx.startActivity(nextVideo);
                break;

            case R.id.videoItemParent:
                Intent nextVideos = new Intent(ctx, VideoScreen.class);
                nextVideos.putExtra("listData", videosModelArrayList);
                nextVideos.putExtra("currentListItem", videosModelArrayList.get((Integer) v.getTag()));
                nextVideos.putExtra("pos", v.getTag().toString());
                nextVideos.putExtra("string", stringFrag);
                ctx.startActivity(nextVideos);
                break;
        }
    }

    public class VideosHolder extends RecyclerView.ViewHolder {
        private ImageView bgImage, play;
        private TextView videoTitle;
        private RelativeLayout videoItemParent;

        public VideosHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.videoTitle);
            bgImage = (ImageView) itemView.findViewById(R.id.bgVideoImage);
            play = (ImageView) itemView.findViewById(R.id.play);
            videoItemParent = (RelativeLayout) itemView.findViewById(R.id.videoItemParent);
        }
    }
}
