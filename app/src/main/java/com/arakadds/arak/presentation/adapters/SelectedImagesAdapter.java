package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO;

public class SelectedImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SelectedImagesAdapter";
    private List<Uri> videoUriList;
    private ArrayList<Bitmap> bitmapArrayList;
    private Context context;
    String categoryId;

    View itemView;
    private CallbackInterface callbackInterface;
    private CallbackDeleteMedia callbackDeleteMedia;
    String currentPhotoPath;
    private Resources resources;

    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position - the position
         * @param intent   - the intent to pass back
         */
        void onHandleSelection(int position, Intent intent, int resultCode);
    }

    public interface CallbackDeleteMedia {

        /**
         * Callback invoked when clicked
         *
         * @param position - the position
         */
        void onHandleDeletingMedia(int position, boolean isImage);
    }


    public SelectedImagesAdapter(List<Uri> videoUriList, ArrayList<Bitmap> bitmapArrayList, Context context, String categoryId, Resources resources, CallbackDeleteMedia callbackDeleteMedia) {

        this.bitmapArrayList = bitmapArrayList;
        this.context = context;
        this.categoryId = categoryId;
        this.videoUriList = videoUriList;

        this.resources = resources;
        this.callbackInterface = callbackInterface;
        this.callbackDeleteMedia = callbackDeleteMedia;
        if (this.bitmapArrayList == null) {
            this.bitmapArrayList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_upoad_image, parent, false);
        return new SelectedImagesAdapter.AdImagesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        switch (Integer.parseInt(categoryId)) {
            case IMAGE:
            case WEBSITE:
                Bitmap bitmap = bitmapArrayList.get(position);
                ((AdImagesHolder) holder).uploadedImageImageView.setImageBitmap(bitmap);
                ((AdImagesHolder) holder).deleteImageImageView.setOnClickListener(v -> callbackDeleteMedia.onHandleDeletingMedia(position, true));
                break;
            case VIDEO:
                //if (videoUriList.size() != position) {} ||
                Uri uri = videoUriList.get(position);
                ((AdImagesHolder) holder).uploadedImageImageView.setVisibility(View.GONE);
                ((AdImagesHolder) holder).uploadedVideoView.setVisibility(View.VISIBLE);
                ((AdImagesHolder) holder).uploadedVideoView.setVideoURI(uri);
                ((AdImagesHolder) holder).uploadedVideoView.setOnPreparedListener(mediaPlayer -> {
                    ((AdImagesHolder) holder).uploadedVideoView.seekTo(1000);
                    ((AdImagesHolder) holder).uploadedVideoView.start();
                    ((AdImagesHolder) holder).uploadedVideoView.pause();  // Pause immediately to stop auto-play
                });
                ((AdImagesHolder) holder).uploadedVideoView.animate().alpha(1);
                ((AdImagesHolder) holder).deleteImageImageView.setOnClickListener(v -> callbackDeleteMedia.onHandleDeletingMedia(position, false));
        }
    }

    @Override
    public int getItemCount() {
        switch (Integer.parseInt(categoryId)) {
            case IMAGE:
            case WEBSITE: {
                return bitmapArrayList.size();
            }
            case VIDEO: {
                return videoUriList.size();
            }
            default:
                return 0;
        }
    }

    class AdImagesHolder extends RecyclerView.ViewHolder {

        private ImageView uploadedImageImageView;
        private ImageView deleteImageImageView;
        private VideoView uploadedVideoView;

        public AdImagesHolder(@NonNull View itemView) {
            super(itemView);
            uploadedImageImageView = itemView.findViewById(R.id.uploaded_image_imageView_id);
            uploadedVideoView = itemView.findViewById(R.id.uploaded_VideoView_id);
            deleteImageImageView = itemView.findViewById(R.id.delete_uploaded_imageView_id);
        }
    }
}
