package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData;
import com.arakadds.arak.presentation.activities.ads.AdsStoryActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.ArrayList;

import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.STORES;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE;
import static com.arakadds.arak.utils.Constants.POSITION;
import static com.arakadds.arak.utils.Constants.PRODUCT_FILES_INFORMATION;

public class HomeSpecialAddsAdapter extends RecyclerView.Adapter<HomeSpecialAddsAdapter.HomeSpecialAddsHolder> implements Serializable {

    private static final String TAG = "HomeAddsAdapter";
    private ArrayList<AdsData> specialAdsDataArrayList;
    private Activity activity;
    private String language;
    private boolean isSpecialActivity;
    private int minutes = 0;
    private int seconds = 0;
    private int hours = 0;
    private Integer limitNumber = 0;
    private boolean hasHours = false;
    private boolean hasMinutes = false;
    private HomeSpecialAdsCallBacks homeSpecialAdsCallBacks;
    private boolean isHomeAd;
    private Resources resources;
    //feed adMob
    private static final int ITEM_FEED_COUNT = 2;
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;

    public HomeSpecialAddsAdapter(ArrayList<AdsData> specialAdsDataArrayList, Integer limitNumber, Activity activity, String language, boolean isSpecialActivity, boolean isHomeAd, Resources resources, HomeSpecialAdsCallBacks homeSpecialAdsCallBacks) {
        this.specialAdsDataArrayList = specialAdsDataArrayList;
        this.activity = activity;
        this.limitNumber = limitNumber;
        this.language = language;
        this.isSpecialActivity = isSpecialActivity;
        this.isHomeAd = isHomeAd;
        this.resources = resources;
        this.homeSpecialAdsCallBacks = homeSpecialAdsCallBacks;
    }

    public interface HomeSpecialAdsCallBacks {
        void onNextPageRequired();
    }

    @NonNull
    @Override
    public HomeSpecialAddsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_home_special_offer, parent, false);
        return new HomeSpecialAddsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSpecialAddsHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.addImageView.setVisibility(View.VISIBLE);
        RequestOptions options = new RequestOptions()
                .centerCrop();
        switch (specialAdsDataArrayList.get(position).getAdCategoryId()) {
            case IMAGE, STORES:
                options.placeholder(R.drawable.image_holder_virtical);
                options.error(R.drawable.image_placeholder);
                break;
            case VIDEO:
                options.placeholder(R.drawable.video_holder_virtical);
                options.error(R.drawable.video_placeholder);
                break;
            case WEBSITE:
                options.placeholder(R.drawable.image_holder_virtical);
                options.error(R.drawable.website_placeholder);
                break;
        }
        try {
            if (specialAdsDataArrayList.get(position).getAdFiles().get(0).getUrl() != null) {
                Glide.with(activity).load(specialAdsDataArrayList.get(position).getAdFiles().get(0).getUrl()).apply(options).into(((HomeSpecialAddsHolder) holder).addImageView);
            } else {
                holder.addImageView.setImageResource(R.drawable.image_placeholder);
            }
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: Exception" + e.getMessage());
        }

        holder.addImageView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AdsStoryActivity.class);
                /*intent.putExtra("selectedAdObject", homeContentsArrayList.get(position));
                intent.putExtra("selectedAdFiles", homeContentsArrayList.get(position).getAdFilesArrayList());
                intent.putExtra("isHomeAd", isHomeAd);*/
            Bundle bundle = new Bundle();
            intent.putExtra(POSITION, position);
            bundle.putSerializable(PRODUCT_FILES_INFORMATION, specialAdsDataArrayList.get(position).getAdFiles());
            intent.putExtras(bundle);
            intent.putExtra("selectedAdObject", (Parcelable) specialAdsDataArrayList.get(position));
            intent.putExtra("isHomeAd", isHomeAd);
            activity.startActivity(intent);
        });
        if (specialAdsDataArrayList.size() - 1 == position) {
            homeSpecialAdsCallBacks.onNextPageRequired();
        }
    }

    @Override
    public int getItemCount() {
        if (limitNumber == null)
            return specialAdsDataArrayList.size();
        else
            return limitNumber;
    }

    class HomeSpecialAddsHolder extends RecyclerView.ViewHolder {
        private TextView typeTextView, titleTextView, timeTextView;
        private ImageView addImageView;
        //private ConstraintLayout adsConstraintLayout;

        public HomeSpecialAddsHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.specail_ad_type_textView_id);
            titleTextView = itemView.findViewById(R.id.specail_ad_company_name_textView_id);
            timeTextView = itemView.findViewById(R.id.specail_ad_time_textView_id);
            addImageView = itemView.findViewById(R.id.specail_ad_defulte_imageView_id);
            // specialAdLabelTextView = itemView.findViewById(R.id.specail_ad_more_ads_textView_id);
            //adsConstraintLayout=itemView.findViewById(R.id.ads_constraint_layout_id);
        }
    }
}
