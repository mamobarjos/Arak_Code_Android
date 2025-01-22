package com.arakadds.arak.presentation.dialogs;

import static com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.FACEBOOK;
import static com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.INSTAGRAM;
import static com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.WEBSITE;
import static com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.WHATS_UP;
import static com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.YOUTUBE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.request.AddAdReviewRequest;
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreProductReviewRequest;
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest;
import com.arakadds.arak.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class AppDialogs {

    private static AppDialogs instance;

    public static AppDialogs getInstance() {
        if (instance == null) {
            instance = new AppDialogs();
        }
        return instance;
    }

    public interface DialogCallBack {
        void openCameraCallback();

        void openGalleryCallback();

        void continuePressedCallback();
    }

    public interface DialogStoreReviewCallBack {
        void onSubmitStoreReviewCallback(AddStoreReviewRequest addStoreReviewRequest);
        void onSubmitStoreProductReviewCallback(AddStoreProductReviewRequest addStoreProductReviewRequest);
        void onSubmitAdReviewCallback(AddAdReviewRequest addAdReviewRequest);
    }

    public interface DialogSocialMediaPlatformsCallBack {
        void onSelectSocialMediaPlatformsCallback(ArrayList<Integer> selectedPlatformsIdsArrayList);
    }

    public static void successDialog(Context context, Activity activity, Resources resources, Class<?> targetedActivity, String contentText, String buttonText, DialogCallBack dialogCallBack) {
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sucess, null);
        final TextView goToMyAdsTextView = dialogView.findViewById(R.id.go_to_my_ads_textView_id);
        final TextView successDescTextView = dialogView.findViewById(R.id.success_desc_textView_id);
        final TextView successTitleTextView = dialogView.findViewById(R.id.seccess_title_textView_id);

        successTitleTextView.setText(resources.getString(R.string.Success_activity_Success));
        successDescTextView.setText(contentText);
        goToMyAdsTextView.setText(buttonText);

        goToMyAdsTextView.setOnClickListener(v -> {
            alertDialog.dismiss();
            dialogCallBack.continuePressedCallback();
            Intent intent = new Intent(context, targetedActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            try {
                if (activity != null)
                    activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.setView(dialogView);
        alertDialog.show();
    }


    public static void addImageDialog(Context context, DialogCallBack dialogCallBack) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_select_media_type);
        dialog.setCancelable(true);

       /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;*/


        TextView titleTextView = dialog.findViewById(R.id.dialog_media_title_textView_id);
        ImageView cameraImageView = dialog.findViewById(R.id.dialog_media_camera_imageView_id);
        ImageView GalleryImageView = dialog.findViewById(R.id.dialog_media_Gallery_imageView_id);
        TextView cameraTitleTextView = dialog.findViewById(R.id.dialog_media_camera_title_TextView_id);
        TextView GalleryTitleTextView = dialog.findViewById(R.id.dialog_media_Gallery_title_TextView_id);

        cameraImageView.setOnClickListener(v -> {
            dialogCallBack.openCameraCallback();

            dialog.dismiss();
        });

        GalleryImageView.setOnClickListener(v -> {
            dialogCallBack.openGalleryCallback();

            dialog.dismiss();
        });

        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }

    public static void addStoreReviewDialog(Context context, Resources resources, Integer storeId, Integer storeProductId, Integer adId, DialogStoreReviewCallBack dialogStoreReviewCallBack) {
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_review, null);
        final TextView feedbackTitleTextView = dialogView.findViewById(R.id.store_profile_feedback_title_textView_id);
        final RatingBar reviewRatingBar = dialogView.findViewById(R.id.store_profile_review_ratingBar_id);
        final EditText feedbackEditText = dialogView.findViewById(R.id.store_profile_feedback_editText_id);
        final TextView feedbackSubmitButton = dialogView.findViewById(R.id.store_profile_feedback_submit_button_id);

        feedbackTitleTextView.setText(resources.getString(R.string.store_profile_activity_Add_Review));
        feedbackEditText.setHint(resources.getString(R.string.store_profile_activity_Enter_review));
        feedbackSubmitButton.setText(resources.getString(R.string.rest_password_activity_Submit));

        feedbackSubmitButton.setOnClickListener(v -> {

            String content = feedbackEditText.getText().toString().trim();
            float rate = reviewRatingBar.getRating();

            if (content.isEmpty()) {
                Toast.makeText(context, resources.getString(R.string.store_profile_activity_enter_Review), Toast.LENGTH_SHORT).show();
                return;
            }
            if (rate == 0) {
                Toast.makeText(context, resources.getString(R.string.store_profile_activity_enter_Review), Toast.LENGTH_SHORT).show();
                return;
            }

            alertDialog.dismiss();
            if (storeId != null) {
                dialogStoreReviewCallBack.onSubmitStoreReviewCallback(new AddStoreReviewRequest(content, rate, storeId));
            } else if (storeProductId != null) {
                dialogStoreReviewCallBack.onSubmitStoreProductReviewCallback(new AddStoreProductReviewRequest(content, rate, storeProductId));
            } else if (adId != null) {
                dialogStoreReviewCallBack.onSubmitAdReviewCallback(new AddAdReviewRequest(content, rate, adId));
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public static void selectSocialMediaDialog(Context context, Resources resources, ArrayList<Integer> selectedPlatformsIdsArrayList, DialogSocialMediaPlatformsCallBack dialogSocialMediaPlatformsCallBack) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_select_social_media);
        dialog.setCancelable(true);

       /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;*/

        TextView titleTextView = dialog.findViewById(R.id.add_social_media_title_textView_id);
        ImageView exitImageView = dialog.findViewById(R.id.add_social_media_exit_ImageView_id);
        Button addButton = dialog.findViewById(R.id.request_service_send_request_Button_id);
        RelativeLayout whatsAppRelativeLayout = dialog.findViewById(R.id.store_profile_whats_app_RelativeLayout_id);
        RelativeLayout facebookRelativeLayout = dialog.findViewById(R.id.store_profile_facebook_RelativeLayout_id);
        RelativeLayout instagramRelativeLayout = dialog.findViewById(R.id.store_profile_instagram_RelativeLayout_id);
        RelativeLayout youtubeRelativeLayout = dialog.findViewById(R.id.store_profile_youtube_RelativeLayout_id);
        RelativeLayout websiteRelativeLayout = dialog.findViewById(R.id.store_profile_website_RelativeLayout_id);

        titleTextView.setText(resources.getString(R.string.dialogs_Social_media_links));
        addButton.setText(resources.getString(R.string.title_add));


        for (int i = 0; i < selectedPlatformsIdsArrayList.size(); i++) {
            int id = selectedPlatformsIdsArrayList.get(i);
            switch (selectedPlatformsIdsArrayList.get(i)) {
                case WHATS_UP: {
                    whatsAppRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
                    break;
                }
                case FACEBOOK: {
                    facebookRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
                    break;
                }
                case INSTAGRAM: {
                    instagramRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
                    break;
                }
                case YOUTUBE: {
                    youtubeRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
                    break;
                }
                case WEBSITE: {
                    websiteRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
                    break;
                }
            }
        }


        exitImageView.setOnClickListener(v -> {
            dialog.dismiss();
        });


        whatsAppRelativeLayout.setOnClickListener(v -> {
            int item = -1;
            for (int i = 0; i < selectedPlatformsIdsArrayList.size(); i++) {
                if (selectedPlatformsIdsArrayList.get(i) == WHATS_UP) {
                    item = i;
                    whatsAppRelativeLayout.setBackgroundResource(0);
                }
            }
            if (item != -1) {
                selectedPlatformsIdsArrayList.remove(item);
            } else {
                selectedPlatformsIdsArrayList.add(WHATS_UP);
                whatsAppRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
            }
        });

        facebookRelativeLayout.setOnClickListener(v -> {
            int item = -1;
            for (int i = 0; i < selectedPlatformsIdsArrayList.size(); i++) {
                if (selectedPlatformsIdsArrayList.get(i) == FACEBOOK) {
                    item = i;
                    facebookRelativeLayout.setBackgroundResource(0);
                }
            }
            if (item != -1) {
                selectedPlatformsIdsArrayList.remove(item);
            } else {
                selectedPlatformsIdsArrayList.add(FACEBOOK);
                facebookRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
            }
        });

        instagramRelativeLayout.setOnClickListener(v -> {
            int item = -1;
            for (int i = 0; i < selectedPlatformsIdsArrayList.size(); i++) {
                if (selectedPlatformsIdsArrayList.get(i) == INSTAGRAM) {
                    item = i;
                    instagramRelativeLayout.setBackgroundResource(0);
                }
            }
            if (item != -1) {
                selectedPlatformsIdsArrayList.remove(item);
            } else {
                selectedPlatformsIdsArrayList.add(INSTAGRAM);
                instagramRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
            }
        });

        youtubeRelativeLayout.setOnClickListener(v -> {
            int item = -1;
            for (int i = 0; i < selectedPlatformsIdsArrayList.size(); i++) {
                if (selectedPlatformsIdsArrayList.get(i) == YOUTUBE) {
                    item = i;
                    youtubeRelativeLayout.setBackgroundResource(0);
                }
            }
            if (item != -1) {
                selectedPlatformsIdsArrayList.remove(item);
            } else {
                selectedPlatformsIdsArrayList.add(YOUTUBE);
                youtubeRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
            }
        });

        websiteRelativeLayout.setOnClickListener(v -> {
            int item = -1;
            for (int i = 0; i < selectedPlatformsIdsArrayList.size(); i++) {
                if (selectedPlatformsIdsArrayList.get(i) == WEBSITE) {
                    item = i;
                    websiteRelativeLayout.setBackgroundResource(0);
                }
            }
            if (item != -1) {
                selectedPlatformsIdsArrayList.remove(item);
            } else {
                selectedPlatformsIdsArrayList.add(WEBSITE);
                websiteRelativeLayout.setBackgroundResource(R.drawable.rounded_boarders_orange_without_background);
            }
        });

        addButton.setOnClickListener(v -> {
            dialogSocialMediaPlatformsCallBack.onSelectSocialMediaPlatformsCallback(selectedPlatformsIdsArrayList);

            dialog.dismiss();
        });

        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }


}
