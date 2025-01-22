package com.arakadds.arak.presentation.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData;
import com.arakadds.arak.presentation.activities.ads.AdDetailUserViewActivity;
import com.arakadds.arak.presentation.activities.ads.AdsStoryActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.ArrayList;

import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.STORES;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE;
import static com.arakadds.arak.utils.Constants.AD_ID;
import static com.arakadds.arak.utils.Constants.CATEGORY_ID;
import static com.arakadds.arak.utils.Constants.IS_HOME;
import static com.arakadds.arak.utils.Constants.POSITION;
import static com.arakadds.arak.utils.Constants.PRODUCT_FILES_INFORMATION;
import static com.arakadds.arak.utils.Constants.selectedAdObject;

public class HomeAddsAdapter extends RecyclerView.Adapter<HomeAddsAdapter.AddsHolder> implements Serializable {

    private static final String TAG = "HomeAddsAdapter";
    private ArrayList<AdsData> adsDataArrayList;
    private Activity activity;
    private String language;
    private boolean isHomeAd;
    private Integer limitNum;
    private PaidAdsCallBacks paidAdsCallBacks;

    public HomeAddsAdapter(ArrayList<AdsData> adsDataArrayList, Integer limitNum, Activity activity, String language, boolean isHomeAd, PaidAdsCallBacks paidAdsCallBacks) {
        this.adsDataArrayList = adsDataArrayList;
        this.limitNum = limitNum;
        this.activity = activity;
        this.language = language;
        this.isHomeAd = isHomeAd;
        this.paidAdsCallBacks = paidAdsCallBacks;
    }

    public interface PaidAdsCallBacks {
        void onNextPageRequired();

        void onSelectAd(AdsData adsData,int position);
    }

    @NonNull
    @Override
    public AddsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_home, parent, false);
        return new AddsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddsHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position:  " + position);
        RequestOptions options = new RequestOptions()
                .centerCrop();
        switch (adsDataArrayList.get(position).getAdCategoryId()) {
            case IMAGE:
                if (language.equals("ar")) {
                    holder.typeTextView.setText("صورة");
                } else {
                    holder.typeTextView.setText("Image");
                }
                options.placeholder(R.drawable.image_placeholder);
                options.error(R.drawable.image_placeholder);
                break;
            case VIDEO:
                if (language.equals("ar")) {
                    holder.typeTextView.setText("فيديو");
                } else {
                    holder.typeTextView.setText("Video");
                }
                options.placeholder(R.drawable.video_placeholder);
                options.error(R.drawable.video_placeholder);
                break;
            case WEBSITE:
                if (language.equals("ar")) {
                    holder.typeTextView.setText("ويب");
                } else {
                    holder.typeTextView.setText("Website");
                }
                options.placeholder(R.drawable.website_placeholder);
                options.error(R.drawable.website_placeholder);
                break;
            case STORES:
                if (language.equals("ar")) {
                    holder.typeTextView.setText("متجر");
                } else {
                    holder.typeTextView.setText("store");
                }
                options.placeholder(R.drawable.image_placeholder);
                options.error(R.drawable.image_placeholder);
                break;
        }
        if (language.equals("ar")) {
            holder.timeTextView.setText(adsDataArrayList.get(position).getDuration() + " ث ");
        } else {
            holder.timeTextView.setText(adsDataArrayList.get(position).getDuration() + " s");
        }

        holder.titleTextView.setText(adsDataArrayList.get(position).getTitle());
        try {
            if (adsDataArrayList.get(position).getAdFiles().get(0).getUrl() != null) {
                Glide.with(activity).load(adsDataArrayList.get(position).getAdFiles().get(0).getUrl()).apply(options).into(holder.addImageView);
            } else {
                holder.addImageView.setImageResource(R.drawable.image_placeholder);
            }
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: Exception" + e.getMessage());
        }

        holder.adsConstraintLayout.setOnClickListener(v -> {
            if (isHomeAd) {
                paidAdsCallBacks.onSelectAd(adsDataArrayList.get(position),position);
            } else {
                Intent intent = new Intent(activity, AdDetailUserViewActivity.class);
                intent.putExtra(AD_ID, String.valueOf(adsDataArrayList.get(position).getId()));
                intent.putExtra(CATEGORY_ID, String.valueOf(adsDataArrayList.get(position).getAdCategoryId()));
                activity.startActivity(intent);
            }
        });

        if (adsDataArrayList.size() - 1 == position) {
            paidAdsCallBacks.onNextPageRequired();
        }
    }

    @Override
    public int getItemCount() {
        if (limitNum == null)
            return adsDataArrayList.size();
        else
            return limitNum;
    }

    class AddsHolder extends RecyclerView.ViewHolder {
        private TextView typeTextView, titleTextView, timeTextView;
        private ImageView addImageView;
        private ConstraintLayout adsConstraintLayout;

        public AddsHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.ad_category_textView_id);
            titleTextView = itemView.findViewById(R.id.ad_title_name_textView_id);
            timeTextView = itemView.findViewById(R.id.ad_time_textView_id);
            addImageView = itemView.findViewById(R.id.ad_defulte_imageView_id);
            adsConstraintLayout = itemView.findViewById(R.id.ads_constraint_layout_id);
        }
    }


}
