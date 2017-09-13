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
import com.eatfit.activity.MyWorkouts;
import com.eatfit.activity.VideoPlayer;
import com.eatfit.activity.VideoScreen;
import com.eatfit.helper.Utils;
import com.eatfit.model.VideosModel;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by appsquadz on 23/6/17.
 */

public class RemainingVideoListAdapter extends RecyclerView.Adapter<RemainingVideoListAdapter.VideosItemHolder> implements View.OnClickListener {

    Context ctx;
    ArrayList<VideosModel> videosModelArrayList;
    String fragmentTitle;

    public RemainingVideoListAdapter(Context context, ArrayList<VideosModel> videosModelArrayList, String fragmentTitle) {
        this.ctx = context;
        this.videosModelArrayList = videosModelArrayList;
        this.fragmentTitle = fragmentTitle;
    }

    @Override
    public VideosItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_item_list, parent, false);
        return new VideosItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideosItemHolder holder, int position) {

        holder.videoTitle.setText(videosModelArrayList.get(position).getName());

        String thumbURL = String.format("http://img.youtube.com/vi/%s/hqdefault.jpg",
                Utils.getYoutubeVideoId(videosModelArrayList.get(position).getLink()));
        Ion.with(ctx)
                .load(thumbURL)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            holder.videoImage.setImageBitmap(result);
                        }
                    }
                });


        if (ctx instanceof MyWorkouts) {
            holder.videoItemParent.setOnClickListener(this);
            holder.videoItemParent.setTag(position);
        } else {
            holder.videoItemParent.setOnClickListener(this);
            holder.videoItemParent.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return videosModelArrayList.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.parent:

               /* if (ctx instanceof VideoScreen) {
                    ((VideoScreen) ctx).changetheListVideo(Integer.parseInt(v.getTag().toString()));
                }*/

                if (ctx instanceof MyWorkouts) {
                    Intent nextVideos = new Intent(ctx, VideoPlayer.class);
                    nextVideos.putExtra("currentListItem", videosModelArrayList.get((Integer) v.getTag()));
                    ctx.startActivity(nextVideos);
                } else {
                    videosModelArrayList.add(((VideoScreen) ctx).videosModel);

                    Intent nextVideos = new Intent(ctx, VideoScreen.class);
                    nextVideos.putExtra("listData", videosModelArrayList);
                    nextVideos.putExtra("currentListItem", videosModelArrayList.get((Integer) v.getTag()));
                    nextVideos.putExtra("pos", v.getTag().toString());
                    nextVideos.putExtra("string", fragmentTitle);
                    ctx.startActivity(nextVideos);
                    ((VideoScreen) ctx).finish();
                }

                break;
        }

    }


    public class VideosItemHolder extends RecyclerView.ViewHolder {
        private ImageView videoImage, play;
        private TextView videoTitle, videoSubTitle;
        private RelativeLayout videoItemParent;

        public VideosItemHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.videoTitleItem);
            videoSubTitle = (TextView) itemView.findViewById(R.id.videoSubTitleItem);
            videoImage = (ImageView) itemView.findViewById(R.id.videoItemImage);
            play = (ImageView) itemView.findViewById(R.id.playButton);
            videoItemParent = (RelativeLayout) itemView.findViewById(R.id.parent);
        }
    }
}
