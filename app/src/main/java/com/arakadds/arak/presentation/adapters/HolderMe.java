package com.arakadds.arak.presentation.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;



/*
 * created by hussam zuriqat at 3/21
 * */

public class HolderMe extends RecyclerView.ViewHolder {

    private TextView time, chatText;
    private ImageView imageMessage;


    private CardView cardImageViewMessage;


    public HolderMe(View v) {
        super(v);
        time = (TextView) v.findViewById(R.id.tv_time);
        chatText = (TextView) v.findViewById(R.id.tv_chat_text);
        imageMessage = (ImageView) v.findViewById(R.id.imageMessage);

        cardImageViewMessage = (CardView) v.findViewById(R.id.cardImageViewMessage);

    }


    public CardView getCardImageViewMessage() {
        return cardImageViewMessage;
    }

    public void setCardImageViewMessage(CardView cardImageViewMessage) {
        this.cardImageViewMessage = cardImageViewMessage;
    }

    public ImageView getImageMessage() {
        return imageMessage;
    }

    public void setImageMessage(ImageView imageMessage) {
        this.imageMessage = imageMessage;
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public TextView getChatText() {
        return chatText;
    }

    public void setChatText(TextView chatText) {
        this.chatText = chatText;
    }
}
