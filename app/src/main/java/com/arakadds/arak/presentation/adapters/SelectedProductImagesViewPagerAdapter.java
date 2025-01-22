package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.ActivityHelper;
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile;

import com.arakadds.arak.presentation.activities.ads.DisplayFullScreenImageActivity;
import com.arakadds.arak.presentation.activities.ads.VideoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;

import static android.widget.ImageView.ScaleType.FIT_CENTER;

public class SelectedProductImagesViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "SelectedProductImagesVi";
    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<StoreProductFile> adFilesArrayList;
    private boolean displayFullScreen;
    private int lastPosition;
    private boolean isStory;

    public SelectedProductImagesViewPagerAdapter(ArrayList<StoreProductFile> adFilesArrayList, Context context, boolean displayFullScreen, int lastPosition, boolean isStory) {
        this.adFilesArrayList = adFilesArrayList;
        this.context = context;
        this.displayFullScreen = displayFullScreen;
        this.lastPosition = lastPosition;
        this.isStory = isStory;
    }

    @Override
    public int getCount() {
        /*if (isStory){
            return 1;
        }else {
            if (adFilesArrayList != null) {
                return adFilesArrayList.size();
            } else {
                return 0;
            }
        }*/

        if (adFilesArrayList != null) {
            return adFilesArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
   /*     if (lastPosition>0){
            position=lastPosition;

        }*/
        try {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.selected_item_media_item_row, null);
            TouchImageView productImage = view.findViewById(R.id.selected_item_media_image_imageView_id);
            ImageView videoIconImageView = view.findViewById(R.id.selected_item_media_video_start_icon_VideoView_id);
            VideoView productVideo = view.findViewById(R.id.selected_item_media_video_VideoView_id);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder);
            if (displayFullScreen) {
                if (adFilesArrayList.get(position).getUrl().contains(".mp4")
                        || adFilesArrayList.get(position).getUrl().contains(".mpeg")) {
                    productVideo.setVisibility(View.GONE);
                    videoIconImageView.setVisibility(View.VISIBLE);
                    productImage.setVisibility(View.VISIBLE);

                    productVideo.setVisibility(View.GONE);
                    videoIconImageView.setVisibility(View.VISIBLE);
                    productImage.setVisibility(View.VISIBLE);
                    productImage.setScaleType(FIT_CENTER);
                    Glide.with(context).load(adFilesArrayList.get(position).getUrl()).apply(options).into(productImage);

                    ViewPager viewPager = (ViewPager) container;
                    viewPager.addView(view, 0);

                    productImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String path=adFilesArrayList.get(position).getUrl();
                            ActivityHelper.goToActivity(context, VideoActivity.class,false,"VideoPath",path);
                        }
                    });

                } else {
                    productVideo.setVisibility(View.GONE);
                    videoIconImageView.setVisibility(View.GONE);
                    productImage.setVisibility(View.VISIBLE);
                    productImage.setScaleType(FIT_CENTER);
                    Glide.with(context).load(adFilesArrayList.get(position).getUrl()).apply(options).into(productImage);
                    //lastPosition=position;
                    ViewPager viewPager = (ViewPager) container;
                    viewPager.addView(view, 0);
                }
            } else {

                if (adFilesArrayList.get(position).getUrl().contains(".mp4")
                        || adFilesArrayList.get(position).getUrl().contains(".mpeg") ) {
                    videoIconImageView.setVisibility(View.VISIBLE);
                }
                Glide.with(context).load(adFilesArrayList.get(position).getUrl()).apply(options).into(productImage);
                ViewPager viewPager = (ViewPager) container;
                viewPager.addView(view, 0);
                productImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DisplayFullScreenImageActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtra("position", position);
                        bundle.putSerializable("product Files Information", adFilesArrayList);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }
            return view;

        } catch (Exception e) {
            Log.d(TAG, "instantiateItem: Exception: " + e.getMessage());
            return null;
        }
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
