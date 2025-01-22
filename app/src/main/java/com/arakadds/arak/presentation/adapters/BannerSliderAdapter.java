package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannerObject;
import com.arakadds.arak.presentation.activities.other.WebViewActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class BannerSliderAdapter extends RecyclerView.Adapter<BannerSliderAdapter.BannerHolder> {

    private ArrayList<BannerObject> bannerObjectArrayList;
    private Context context;

    public BannerSliderAdapter(ArrayList<BannerObject> bannerObjectArrayList, Context context) {
        this.bannerObjectArrayList = bannerObjectArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_slider_item, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_holder_virtical);
        Glide.with(context).load(bannerObjectArrayList.get(position).getImgUrl())
                .apply(options).into(holder.sliderImageView);
        holder.sliderImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("web_Url", bannerObjectArrayList.get(position).getWebsiteUrl());
            intent.putExtra("page_title", "Arak");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bannerObjectArrayList != null ? bannerObjectArrayList.size() : 0;
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {

        ImageView sliderImageView;

        public BannerHolder(@NonNull View itemView) {
            super(itemView);
            sliderImageView = itemView.findViewById(R.id.slider_imageView_id);
        }
    }
}
