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
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class UsersReviewsAdapter extends RecyclerView.Adapter<UsersReviewsAdapter.UserReviewHolder> {

    private ArrayList<StoreProductReview> storeProductReviewArrayList;
    private Context context;
    private ProgressDialog dialog;
    private Resources resources;
    private int userId;
    private ReviewsEvents reviewsEvents;
    private String name;
    private int limitNum;
    boolean isProductReviews;

    public UsersReviewsAdapter(ArrayList<StoreProductReview> storeProductReviewArrayList, String name, int userId, int limitNum, Context context,
                               Resources resources, ReviewsEvents reviewsEvents, boolean isProductReviews) {
        this.storeProductReviewArrayList = storeProductReviewArrayList;
        this.context = context;
        this.resources = resources;
        this.reviewsEvents = reviewsEvents;
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

    public interface ReviewsEvents {
        void onDeleteStoreReviewCallback(int id, int position, ImageView deleteUserReview);

        void onDeleteProductReviewCallback(int id, int position, ImageView deleteUserReview);
    }

    @NonNull
    @Override
    public UserReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_item_row, parent, false);

        return new UsersReviewsAdapter.UserReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewHolder holder, int position) {
        try {
            StoreProductReview review = storeProductReviewArrayList.get(position);

            // Handle user name
            if (review != null && review.getUser() != null) {
                holder.userNameTextView.setText(review.getUser().getFullName());

                // Handle user image
                String avatarUrl = review.getUser().getImgAvatar();
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder);

                Glide.with(context)
                        .load(avatarUrl != null ? avatarUrl : "")
                        .apply(options)
                        .into(holder.userImageImageView);

                // Handle delete button visibility
                try {
                    if (review.getUser().getId() == userId) {
                        holder.deleteUserReview.setVisibility(View.VISIBLE);
                    } else {
                        holder.deleteUserReview.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    holder.deleteUserReview.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            } else {
                holder.userNameTextView.setText("you");
                holder.userImageImageView.setImageResource(R.drawable.image_placeholder);
                holder.deleteUserReview.setVisibility(View.GONE);
            }

            // Handle rating
            if (review != null && review.getRating() != null) {
                try {
                    holder.ratingBar.setRating(Float.parseFloat(review.getRating()));
                } catch (NumberFormatException e) {
                    holder.ratingBar.setRating(0);
                    e.printStackTrace();
                }
            } else {
                holder.ratingBar.setRating(0);
            }

            // Handle store name
            holder.storeNameTextView.setText(name != null ? name : "");

            // Handle review content
            if (review != null && review.getContent() != null) {
                holder.userReviewContentTextView.setText(review.getContent());
                holder.userReviewContentTextView.setVisibility(View.VISIBLE);
            } else {
                holder.userReviewContentTextView.setVisibility(View.GONE);
            }

            // Handle delete button click
            holder.deleteUserReview.setOnClickListener(v -> showDeleteDialog(position, holder.deleteUserReview));

        } catch (Exception e) {
            e.printStackTrace();
            // Set default values in case of any exception
            holder.userNameTextView.setText("you");
            holder.userImageImageView.setImageResource(R.drawable.image_placeholder);
            holder.ratingBar.setRating(0);
            holder.userReviewContentTextView.setVisibility(View.GONE);
            holder.deleteUserReview.setVisibility(View.GONE);
        }
    }

    // Separate method for delete dialog
    private void showDeleteDialog(int position, ImageView deleteButton) {
        new AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.dialogs_Delete_entry))
                .setMessage(resources.getString(R.string.dialogs_delete_this_entry))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (isProductReviews) {
                        reviewsEvents.onDeleteProductReviewCallback(
                                storeProductReviewArrayList.get(position).getId(),
                                position,
                                deleteButton
                        );
                    } else {
                        reviewsEvents.onDeleteStoreReviewCallback(
                                storeProductReviewArrayList.get(position).getId(),
                                position,
                                deleteButton
                        );
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
