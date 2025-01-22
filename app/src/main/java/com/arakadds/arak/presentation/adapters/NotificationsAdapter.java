package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.TimeShow;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.notification.NotificationObject;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsHolder> {
    private static final String TAG = "UserTransactionsAdapter";

    private ArrayList<NotificationObject> notificationsArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private NotificationsCallBacks notificationsCallBacks;
    TimeShow timeShow = new TimeShow();

    public NotificationsAdapter(ArrayList<NotificationObject> notificationsArrayList, Context context, String language, Resources resources,NotificationsCallBacks notificationsCallBacks) {
        this.context = context;
        this.notificationsArrayList = notificationsArrayList;
        this.language = language;
        this.resources = resources;
        this.notificationsCallBacks = notificationsCallBacks;
    }

    public interface NotificationsCallBacks {
        void onNextPageRequired();
    }

    @NonNull
    @Override
    public NotificationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_notification, parent, false);
        return new NotificationsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsHolder holder, int position) {

        if (notificationsArrayList.get(position).isRead()) {
            //unread
            try {
                holder.NotificationConstraintLayout.setBackgroundColor(Color.parseColor("#FFF0CC"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (notificationsArrayList.get(position).isRead()) {
            //read
            holder.NotificationConstraintLayout.setBackgroundResource(0);
        }
        holder.NotificationTitleTextView.setText(notificationsArrayList.get(position).getTitle());
        holder.NotificationBodyTextView.setText(notificationsArrayList.get(position).getDescription());
        holder.NotificationImageView.setImageResource(R.drawable.small_logo);
        holder.NotificationTimeTextView.setText(timeShow.parseDate(notificationsArrayList.get(position).getCreatedAt()));

        if (notificationsArrayList.size() - 1 == position) {
            notificationsCallBacks.onNextPageRequired();
        }
    }

    @Override
    public int getItemCount() {
        if (notificationsArrayList != null) {
            return notificationsArrayList.size();
        }
        return 0;
    }

    class NotificationsHolder extends RecyclerView.ViewHolder {

        private ImageView NotificationImageView;
        private TextView NotificationTitleTextView;
        private TextView NotificationBodyTextView;
        private TextView NotificationTimeTextView;
        private ConstraintLayout NotificationConstraintLayout;

        public NotificationsHolder(@NonNull View itemView) {
            super(itemView);
            NotificationImageView = itemView.findViewById(R.id.notification_user_image_imageView_id);
            NotificationTitleTextView = itemView.findViewById(R.id.notification_title_textView_id);
            NotificationBodyTextView = itemView.findViewById(R.id.notification_body_textView_id);
            NotificationTimeTextView = itemView.findViewById(R.id.notification_time_textView_id);
            NotificationConstraintLayout = itemView.findViewById(R.id.notifications_ConstraintLayout_id);
        }
    }
}
