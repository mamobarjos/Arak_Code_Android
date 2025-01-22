package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannerObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class EllectionSliderAdapter extends RecyclerView.Adapter<EllectionSliderAdapter.BannerHolder> {

    private ArrayList<BannerObject> electionBannerDataArrayList;
    private EllectionSliderCallBack ellectionSliderCallBack;
    private Context context;

    public EllectionSliderAdapter(ArrayList<BannerObject> electionBannerDataArrayList, EllectionSliderCallBack ellectionSliderCallBack, Context context) {
        this.electionBannerDataArrayList = electionBannerDataArrayList;
        this.ellectionSliderCallBack = ellectionSliderCallBack;
        this.context = context;
    }

    public interface EllectionSliderCallBack {
        void onEllectionSliderClickEvent(BannerObject electionPeopleData, int position);
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
        Glide.with(context).load(electionBannerDataArrayList.get(position).getImgUrl())
                .apply(options).into(holder.sliderImageView);
        holder.sliderImageView.setOnClickListener(v -> {
            ellectionSliderCallBack.onEllectionSliderClickEvent(electionBannerDataArrayList.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return electionBannerDataArrayList != null ? electionBannerDataArrayList.size() : 0;
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {

        ImageView sliderImageView;

        public BannerHolder(@NonNull View itemView) {
            super(itemView);
            sliderImageView = itemView.findViewById(R.id.slider_imageView_id);
        }
    }
}
