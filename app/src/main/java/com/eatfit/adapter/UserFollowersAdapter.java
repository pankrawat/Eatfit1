package com.eatfit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.activity.FollowersFollowing;
import com.eatfit.helper.CircleImageView;
import com.eatfit.model.FollowersModel;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by appsquadz on 12/7/17.
 */

public class UserFollowersAdapter extends RecyclerView.Adapter<UserFollowersAdapter.FollowersAdapterHolder> {

    ArrayList<FollowersModel> followersArrayList;
    Context context;

    public UserFollowersAdapter(Context context, ArrayList<FollowersModel> followersModelArrayList) {
        this.context = context;
        this.followersArrayList = followersModelArrayList;
    }

    @Override
    public FollowersAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_item, parent, false);
        return new FollowersAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(final FollowersAdapterHolder holder, int position) {

        holder.userName.setText(followersArrayList.get(position).getName());
        Ion.with(context)
                .load(followersArrayList.get(position).getProfileImage())
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
    }

    @Override
    public int getItemCount() {
        return followersArrayList.size();
    }

    public class FollowersAdapterHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;

        public FollowersAdapterHolder(View itemView) {
            super(itemView);

            userImage = (CircleImageView) itemView.findViewById(R.id.userFollowersImage);
            userName = (TextView) itemView.findViewById(R.id.userFollowersName);
        }
    }
}
