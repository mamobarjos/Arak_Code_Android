package com.arakadds.arak.presentation.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.AdReviews;

import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class AdsReviewsAdapter extends RecyclerView.Adapter<AdsReviewsAdapter.UserReviewHolder> {

    private ArrayList<AdReviews> adReviewsArrayList;
    private Context context;
    private ProgressDialog dialog;
    private Resources resources;
    private int userId;
    private AdsReviewsEvents adsReviewsEvents;
    private String name;
    private int limitNum;
    boolean isProductReviews;

    public AdsReviewsAdapter(ArrayList<AdReviews> adReviewsArrayList, String name, int userId, int limitNum, Context context,
                             Resources resources, AdsReviewsEvents adsReviewsEvents, boolean isProductReviews) {
        this.adReviewsArrayList = adReviewsArrayList;
        this.context = context;
        this.resources = resources;
        this.adsReviewsEvents = adsReviewsEvents;
        this.name = name;
        this.isProductReviews = isProductReviews;
        this.userId = userId;
        this.limitNum = limitNum;
        try {
            dialog = new ProgressDialog(context);
            dialog.setMessage(this.resources.getString(R.string.dialogs_loading_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface AdsReviewsEvents {
        void onDeleteStoreReviewCallback(int id, int position, ImageView deleteUserReview);

        void onDeleteProductReviewCallback(int id, int position, ImageView deleteUserReview);
    }

    @NonNull
    @Override
    public UserReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_item_row, parent, false);

        return new AdsReviewsAdapter.UserReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewHolder holder, int position) {

        if (adReviewsArrayList.get(position).getUser() != null) {
            holder.userNameTextView.setText(adReviewsArrayList.get(position).getUser().getFullName());
        }

        if (adReviewsArrayList.get(position).getRating() != null) {
            //holder.rateValueTextView.setText(storeProductReviewArrayList.get(position).getRating());
            holder.ratingBar.setRating(Float.parseFloat(adReviewsArrayList.get(position).getRating()));
        }

        holder.storeNameTextView.setText(name);
        holder.userReviewContentTextView.setText(adReviewsArrayList.get(position).getContent());

        //holder.storeNameTextView.setText(storeProductReviewArrayList.get(position).);
        try {
            if (adReviewsArrayList.get(position).getUser().getId() == userId) {
                holder.deleteUserReview.setVisibility(View.VISIBLE);
            } else {
                holder.deleteUserReview.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.deleteUserReview.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.dialogs_Delete_entry))
                .setMessage(resources.getString(R.string.dialogs_delete_this_entry))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                    if (isProductReviews) {
                        adsReviewsEvents.onDeleteProductReviewCallback(adReviewsArrayList.get(position).getId(), position, holder.deleteUserReview);
                    } else {
                        adsReviewsEvents.onDeleteStoreReviewCallback(adReviewsArrayList.get(position).getId(), position, holder.deleteUserReview);
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());

        try{
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder);
            Glide.with(context).load(Objects.requireNonNull(adReviewsArrayList.get(position).getUser()).getImgAvatar()).apply(options).into(holder.userImageImageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return limitNum;
    }

    protected class UserReviewHolder extends RecyclerView.ViewHolder {

        private ImageView userImageImageView;
        private ImageView deleteUserReview;
        private TextView userNameTextView;
        private TextView storeNameTextView;
        private MaterialRatingBar ratingBar;
        private TextView userReviewContentTextView;

        public UserReviewHolder(@NonNull View itemView) {
            super(itemView);

            userImageImageView = itemView.findViewById(R.id.user_review_imageView_id);
            deleteUserReview = itemView.findViewById(R.id.user_review_delete_icon_imageView_id);
            userNameTextView = itemView.findViewById(R.id.user_review_user_name_textView_id);
            ratingBar = itemView.findViewById(R.id.user_review_ratingBar_id);
            storeNameTextView = itemView.findViewById(R.id.user_review_store_name_textView_id);
            userReviewContentTextView = itemView.findViewById(R.id.user_review_text_content_textView_id);
        }
    }
}
