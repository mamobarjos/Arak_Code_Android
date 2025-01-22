package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.TimeShow;
import com.arakadds.arak.model.message.Message;
import com.arakadds.arak.model.message.User;
import com.arakadds.arak.presentation.activities.chat.UserService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<Message> messageList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private User destenationUser;
    private String userIdAccount;
    private ArrayList<User> userArrayList = new ArrayList<>();

    // data is passed into the constructor
    public UserAdapter(Context context, List<Message> messageList,String userIdAccount, ItemClickListener mClickListener) {
        try {
            this.mInflater = LayoutInflater.from(context);
            setClickListener(mClickListener);
            this.messageList = messageList;
            this.userIdAccount = userIdAccount;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public LayoutInflater getmInflater() {
        return mInflater;
    }

    public void updateList(List<Message> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Message message = messageList.get(position);

        UserService userService = new UserService();

        if (message.isSeen() == false && message.getTo_Id().equals(userIdAccount)) {
            holder.dot.setVisibility(View.VISIBLE);
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#dacef4"));
        } else {
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

            holder.dot.setVisibility(View.GONE);
        }

        userService.getUser(message.chatPartnerId(userIdAccount), new UserService.UserServiceCallBack() {

            @Override
            public void userCallBack(User user) {

                //Log.d(TAG, "userCallBack() returned: " + user.toString());
                userArrayList.add(user);
                if (userArrayList.get(position) != null) {
                    destenationUser = userArrayList.get(position);
                    message.setUser(userArrayList.get(position));
                    holder.name.setText(userArrayList.get(position).getFullname() == null ? "" : userArrayList.get(position).getFullname());
                    if (userArrayList.get(position).getImg_avatar() != null && !userArrayList.get(position).getImg_avatar().isEmpty()) {
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.image_holder_virtical)
                                .error(R.drawable.image_holder_virtical);
                        Glide.with(holder.profile).load(userArrayList.get(position).getImg_avatar()).apply(options).into(holder.profile);
                    }
                }
            }
        });
        holder.time.setText(new TimeShow().getRelationTime(message.getTimestamp()));
        holder.lastMessage.setText(message.getText() == null ? "Image Sent" : message.getText());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mClickListener.onItemClick(holder.constraintLayout, position, userArrayList.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, lastMessage;
        ImageView profile, delete, dot;
        View read;
        ConstraintLayout constraintLayout;

        ViewHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.chat_item_view_user_imageView_id);
            name = itemView.findViewById(R.id.chat_item_view_user_name_textView_id);
            lastMessage = itemView.findViewById(R.id.chat_item_view_last_message_textView_id);
            time = itemView.findViewById(R.id.chat_item_view_message_time_textView_id);
            constraintLayout = itemView.findViewById(R.id.custom_user_ConstraintLayout_id);
            dot = itemView.findViewById(R.id.custom_user_dot_imageView_id);
        }


    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, User user);
    }

}