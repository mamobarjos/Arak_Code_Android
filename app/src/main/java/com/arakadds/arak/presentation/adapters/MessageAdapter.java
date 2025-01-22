package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.ActivityHelper;
import com.arakadds.arak.common.helper.TimeShow;
import com.arakadds.arak.model.message.Message;
import com.arakadds.arak.model.message.User;
import com.arakadds.arak.presentation.activities.ads.DisplayFullScreenImageActivity;
import com.arakadds.arak.presentation.activities.chat.UserService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.List;

/*
 * created by hussam zuriqat at 5/21
 * */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // The items to display in your RecyclerView
    private List<Message> items;
    private Context mContext;
    private String userId;
    private UserService userService;
    private final int YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(Context context, List<Message> items, String userId) {
        this.mContext = context;
        this.userId = userId;
        this.items = items;
        userService = new UserService();
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getSender_Id().equals(userId)) {
            return ME;
        }
        return YOU;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
                viewHolder = new HolderYou(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
                viewHolder = new HolderMe(v);
                break;
        }
        return viewHolder;
    }

    public void addItem(List<Message> item) {
        items.clear();
        items.addAll(item);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case YOU:
                HolderYou vh2 = (HolderYou) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                HolderMe vh = (HolderMe) viewHolder;
                configureViewHolder3(vh, position);
                break;
        }
    }

    private void configureViewHolder3(HolderMe vh1, int position) {
        TimeShow timeShow = new TimeShow();
        vh1.getTime().setText(timeShow.getRelationTime(items.get(position).getTimestamp()));
        if (items.get(position).getText() == null && items.get(position).getImageUrl() != null) {
            vh1.getCardImageViewMessage().setVisibility(View.VISIBLE);
            vh1.getCardImageViewMessage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityHelper.goToActivity(mContext, DisplayFullScreenImageActivity.class,false,"image_path",items.get(position).getImageUrl());
                }
            });
            vh1.getChatText().setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.image_holder_virtical)
                    .error(R.drawable.image_holder_virtical);
            Glide.with(vh1.itemView).load(items.get(position).getImageUrl()).apply(options).into(vh1.getImageMessage());
        }  else {
            vh1.getCardImageViewMessage().setVisibility(View.GONE);
            vh1.getChatText().setVisibility(View.VISIBLE);
            vh1.getChatText().setText(items.get(position).getText());
        }
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
        TimeShow timeShow = new TimeShow();
        vh1.getTime().setText(timeShow.getRelationTime(items.get(position).getTimestamp()));
        if (items.get(position).getText() == null && items.get(position).getImageUrl() != null) {
            vh1.getCardImageViewMessage().setVisibility(View.VISIBLE);
            vh1.getCardImageViewMessage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityHelper.goToActivity(mContext, DisplayFullScreenImageActivity.class,false,"image_path",items.get(position).getImageUrl());
                }
            });
            vh1.getChatText().setVisibility(View.GONE);
           // Picasso.get().load(items.get(position).getImageUrl()).error(R.drawable.ic_profile_placeholder).into(vh1.getImageMessage());
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.image_holder_virtical)
                    .error(R.drawable.image_holder_virtical);
            Glide.with(vh1.itemView).load(items.get(position).getImageUrl()).apply(options).into(vh1.getImageMessage());

        } else {
            vh1.getChatText().setText(items.get(position).getText());
        }

        userService.getUser(items.get(position).getSender_Id(), new UserService.UserServiceCallBack() {
            @Override
            public void userCallBack(User user) {
                if (user != null) {
                   // vh1.get.setText(user.getFullname() == null ? "" : user.getFullname());
                    if (user.getImg_avatar() != null && !user.getImg_avatar().isEmpty()){
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.image_holder_virtical)
                                .error(R.drawable.image_holder_virtical);
                        try {
                            Glide.with(vh1.itemView).load(user.getImg_avatar()).apply(options).into(vh1.getUserImage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
