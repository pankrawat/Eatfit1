package com.eatfit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.activity.Comments;
import com.eatfit.helper.CircleImageView;
import com.eatfit.model.CommentsModel;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by appsquadz on 12/7/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {

    ArrayList<CommentsModel> commentsModelArrayList;
    Context context;

    public CommentsAdapter(Context comments, ArrayList<CommentsModel> commentsArrayList) {
        this.commentsModelArrayList = commentsArrayList;
        this.context = comments;
    }

    @Override
    public CommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);
        return new CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsHolder holder, int position) {
        holder.userName.setText(commentsModelArrayList.get(position).getFirstName() + " " +
                commentsModelArrayList.get(position).getLastName());

        holder.commentPost.setText(commentsModelArrayList.get(position).getComment());

        Ion.with(context).load(commentsModelArrayList.get(position).getProfileImage())
                .asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e == null)
                    holder.userImage.setImageBitmap(result);
                else
                    holder.userImage.setImageResource(R.mipmap.profile_green);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsModelArrayList.size();
    }

    public class CommentsHolder extends RecyclerView.ViewHolder {
        private TextView commentPost, userName;
        private CircleImageView userImage;

        public CommentsHolder(View itemView) {
            super(itemView);

            commentPost = (TextView) itemView.findViewById(R.id.userCommentText);
            userImage = (CircleImageView) itemView.findViewById(R.id.userCommentImage);
            userName = (TextView) itemView.findViewById(R.id.userCommentName);
        }
    }
}
