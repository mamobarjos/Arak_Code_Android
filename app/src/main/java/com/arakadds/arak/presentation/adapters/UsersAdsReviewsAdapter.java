package com.arakadds.arak.presentation.adapters;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class UsersAdsReviewsAdapter extends RecyclerView.Adapter<UsersAdsReviewsAdapter.UserReviewHolder> {
    private ArrayList<AdReviews> adReviewsArrayList;
    private Context context;
    private ProgressDialog dialog;
    private Resources resources;
    private int userId;
    private ReviewsEvents reviewsEvents;
    boolean isProductReviews;

    public UsersAdsReviewsAdapter(ArrayList<AdReviews> adReviewsArrayList, int userId, Context context,
                                  Resources resources, ReviewsEvents reviewsEvents, boolean isProductReviews) {
        this.adReviewsArrayList = adReviewsArrayList;
        this.context = context;
        this.resources = resources;
        this.reviewsEvents = reviewsEvents;
        this.isProductReviews = isProductReviews;
        this.userId = userId;
        try {
            dialog = new ProgressDialog(context);
            dialog.setMessage(this.resources.getString(R.string.dialogs_loading_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ReviewsEvents {
        void onDeleteStoreReviewCallback(int id, int position, ImageView deleteUserReview);

        void onDeleteProductReviewCallback(int id, int position, ImageView deleteUserReview);

        void onDeleteAdReviewCallback(int id, int position, ImageView deleteUserReview);
    }

    @NonNull
    @Override
    public UserReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_item_row, parent, false);

        return new UsersAdsReviewsAdapter.UserReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewHolder holder, int position) {
        try {
            if (adReviewsArrayList.get(position).getUser() != null) {
                holder.userNameTextView.setText(adReviewsArrayList.get(position).getUser().getFullName());
            }
            //holder.rateValueTextView.setText(adReviewsArrayList.get(position).getRating().toString());
            holder.userReviewContentTextView.setText(adReviewsArrayList.get(position).getContent());
            try {
                holder.ratingBar.setRating(Float.parseFloat(adReviewsArrayList.get(position).getRating()));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
            //holder.storeNameTextView.setText(storeProductReviewArrayList.get(position).);

            if (adReviewsArrayList.get(position).getUser().getId() == userId) {
                holder.deleteUserReview.setVisibility(View.VISIBLE);
            } else {
                holder.deleteUserReview.setVisibility(View.GONE);
            }

            holder.deleteUserReview.setOnClickListener(v -> {
                reviewsEvents.onDeleteAdReviewCallback(adReviewsArrayList.get(position).getId(), position, holder.deleteUserReview);
            });

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder);
            Glide.with(context).load(adReviewsArrayList.get(position).getUser().getImgAvatar()).apply(options).into(holder.userImageImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (adReviewsArrayList != null) {
            return adReviewsArrayList.size();
        }
        return 0;
    }

    protected class UserReviewHolder extends RecyclerView.ViewHolder {

        private ImageView userImageImageView;
        private ImageView deleteUserReview;
        private TextView userNameTextView;
       private TextView storeNameTextView;
        private MaterialRatingBar ratingBar;
       // private TextView rateValueTextView;
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
