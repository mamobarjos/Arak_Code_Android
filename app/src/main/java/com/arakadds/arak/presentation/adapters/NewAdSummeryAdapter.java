package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.ActivityHelper;
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile;
import com.arakadds.arak.presentation.activities.ads.DisplayFullScreenImageActivity;
import com.arakadds.arak.presentation.activities.ads.VideoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static com.arakadds.arak.utils.Constants.POSITION;
import static com.arakadds.arak.utils.Constants.PRODUCT_FILES_INFORMATION;

public class NewAdSummeryAdapter extends RecyclerView.Adapter<NewAdSummeryAdapter.ImagesHolder> {

    private ArrayList<Bitmap> bitmapArrayList;
    private ArrayList<Uri> videoUriList;
    private ArrayList<StoreProductFile> storeProductFile;
    private Context context;
    private String categoryId;
    private boolean isVideo;
    private boolean isAdDetailsVideo=false;

    public NewAdSummeryAdapter(ArrayList<Bitmap> bitmapArrayList, Context context, String categoryId) {
        this.bitmapArrayList = bitmapArrayList;
        this.context = context;
        this.categoryId = categoryId;
    }

    public NewAdSummeryAdapter(ArrayList<Uri> videoUriList, Context context, String categoryId, boolean isVideo) {
        this.videoUriList = videoUriList;
        this.context = context;
        this.categoryId = categoryId;
        this.isVideo = isVideo;
    }

    public NewAdSummeryAdapter(ArrayList<StoreProductFile> storeProductFile, Context context, String categoryId, boolean isVideo, boolean isAdDetailsVideo, String flag) {
        this.storeProductFile = storeProductFile;
        this.context = context;
        this.categoryId = categoryId;
        this.isAdDetailsVideo = isAdDetailsVideo;
        this.isVideo = isVideo;
    }

    @NonNull
    @Override
    public ImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_summery_image, parent, false);

        return new NewAdSummeryAdapter.ImagesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesHolder holder, @SuppressLint("RecyclerView") int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop();
        switch (Integer.parseInt(categoryId)) {
            case 1:
            case 3:
                options.placeholder(R.drawable.image_holder_virtical);
                options.error(R.drawable.image_holder_virtical);
                holder.mImageView.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (isVideo) {
                    if (isAdDetailsVideo) {
                        holder.videoPlayImageView.setVisibility(View.VISIBLE);
                        holder.mVideoView.setVisibility(View.VISIBLE);
                        Glide.with(context).load(storeProductFile.get(position).getUrl()).apply(options).into(holder.mImageView);
                    } else {
                        holder.mImageView.setVisibility(View.GONE);
                        holder.mVideoView.setVisibility(View.VISIBLE);
                        holder.videoPlayImageView.setVisibility(View.VISIBLE);
                        holder.mVideoView.setVideoURI(videoUriList.get(position));
                        holder.mVideoView.animate().alpha(1);
                        holder.mVideoView.seekTo(1000);
                        holder.mVideoView.pause();
                    }
                } else {
                    options.placeholder(R.drawable.video_holder_virtical);
                    options.error(R.drawable.video_holder_virtical);
                }
                break;
        }

        if (bitmapArrayList != null && storeProductFile == null && !isVideo) {
            holder.mImageView.setImageBitmap(bitmapArrayList.get(position));
        } else if (bitmapArrayList == null && storeProductFile != null) {
            Glide.with(context).load(storeProductFile.get(position).getUrl()).apply(options).into(holder.mImageView);
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storeProductFile != null) {
                    Intent intent = new Intent(context, DisplayFullScreenImageActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra(POSITION, position);
                    bundle.putSerializable(PRODUCT_FILES_INFORMATION, storeProductFile);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });

        holder.videoPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isAdDetailsVideo){
                        ActivityHelper.goToActivity(context, VideoActivity.class, false, "VideoPath", videoUriList.get(position).getPath());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (categoryId.equals("2")) {
            if (videoUriList != null) {
                itemCount = videoUriList.size();
            } else if (storeProductFile != null) {
                itemCount = storeProductFile.size();
            }
        } else {
            if (bitmapArrayList != null && storeProductFile == null) {
                itemCount = bitmapArrayList.size();
            } else if (bitmapArrayList == null && storeProductFile != null) {
                itemCount = storeProductFile.size();
            }
        }
        return itemCount;
    }

    public class ImagesHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private ImageView videoPlayImageView;
        private VideoView mVideoView;

        public ImagesHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.summery_ad_image_imageView_id);
            mVideoView = itemView.findViewById(R.id.summery_ad_video_VideoView_id);
            videoPlayImageView = itemView.findViewById(R.id.summery_ad_video_play_imageView_id);
        }
    }
}
