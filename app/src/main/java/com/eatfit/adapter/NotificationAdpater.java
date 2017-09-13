package com.eatfit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.helper.CircleImageView;
import com.eatfit.model.CommentsModel;
import com.eatfit.model.Notification;

import java.util.ArrayList;

/**
 * Created by appsquadz on 6/9/17.
 */

public class NotificationAdpater extends RecyclerView.Adapter<NotificationAdpater.NotificationsHolder> {

    private Context context;
    private ArrayList<Notification> notificationModelArrayList;

    public NotificationAdpater(Context context, ArrayList<Notification> notificationArrayList) {
        this.notificationModelArrayList = notificationArrayList;
        this.context = context;
    }

    @Override
    public NotificationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationsHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsHolder holder, int position) {
        holder.notificationText.setText(notificationModelArrayList.get(position).getMessage());
        holder.time.setText(DateUtils.getRelativeTimeSpanString(context,
                Long.parseLong(notificationModelArrayList.get(position).getTime())));

        if ((position % 2) == 0) {
            holder.notificationImage.setImageResource(R.mipmap.food1);
        } else {
            holder.notificationImage.setImageResource(R.mipmap.food2);
        }
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();
    }

    public class NotificationsHolder extends RecyclerView.ViewHolder {
        private TextView notificationText, time;
        private ImageView notificationImage;

        public NotificationsHolder(View itemView) {
            super(itemView);

            notificationText = (TextView) itemView.findViewById(R.id.textNotify);
            time = (TextView) itemView.findViewById(R.id.timeNotify);
            notificationImage = (ImageView) itemView.findViewById(R.id.imageviewIcon);
        }
    }
}
