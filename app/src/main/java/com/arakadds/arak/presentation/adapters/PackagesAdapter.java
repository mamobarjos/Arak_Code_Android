package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.ActivityHelper;
import com.arakadds.arak.common.preferaence.IPreferenceHelper;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages;
import com.arakadds.arak.presentation.activities.ads.CreateNewAdActivity;
import com.arakadds.arak.presentation.activities.ads.CustomPackage.CustomPackageActivity;
import com.arakadds.arak.presentation.activities.payments.PaymentOptionsActivity;

import java.util.List;

import static com.arakadds.arak.utils.Constants.AD_PACKAGE_ID;
import static com.arakadds.arak.utils.Constants.CATEGORY_ID;
import static com.arakadds.arak.utils.Constants.NEW_AD_DATA;
import static com.arakadds.arak.utils.Constants.PACKAGE_TIME_LONG_ID;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.PackagesHolder> {

    private static final String TAG = "PackagesAdapter";
    private List<AdPackages> adPackagesList;
    private Context context;
    private String cateId;
    private IPreferenceHelper preferenceHelper;
    private Resources resources;

    public PackagesAdapter(List<AdPackages> adPackagesList, Context context, String cateId, IPreferenceHelper preferenceHelper, Resources resources) {
        this.adPackagesList = adPackagesList;
        this.context = context;
        this.cateId = cateId;
        this.preferenceHelper = preferenceHelper;
        this.resources = resources;
    }

    @NonNull
    @Override
    public PackagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_packages, parent, false);

        return new PackagesAdapter.PackagesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PackagesHolder holder, @SuppressLint("RecyclerView") int position) {
        /*if (position == adPackagesList.size()) {
            holder.packagesConstraintLayout.setBackgroundColor(Color.parseColor("#FF6E2E"));
            holder.priceTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.priceTextView.setText(resources.getString(R.string.Category_Packages_activity_Custom_Package));
            holder.priceTextView.setTextColor(Color.parseColor("#FF6E2E"));
            holder.reachNumberLinearLayout.setVisibility(View.INVISIBLE);
            holder.imagesNumberLinearLayout.setVisibility(View.INVISIBLE);
            holder.numberSecoundsLinearLayout.setVisibility(View.INVISIBLE);
            holder.customPackageImageView.setVisibility(View.VISIBLE);

            holder.packagesConstraintLayout.setOnClickListener(v -> {
                Intent intent=new Intent(context,CustomPackageActivity.class);
             //   intent.putExtra(AD_PACKAGE_ID, adPackagesList.get(position));
                intent.putExtra(CATEGORY_ID,cateId);
                context.startActivity(intent);
            });
        } else {*/
        holder.reachNumberTextView.setText(adPackagesList.get(position).getReach() + " " + resources.getString(R.string.Category_Packages_activity_Reach));
        try {
            holder.imagesNumberTextView.setText(adPackagesList.get(position).getNumberOfImages() + " " + resources.getString(R.string.Category_Packages_activity_Image));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onBindViewHolder: Exception: " + e.getMessage());
            holder.imagesNumberTextView.setText(adPackagesList.get(position).getNumberOfImages() + " " + resources.getString(R.string.Category_Packages_activity_Image));
        }
        holder.priceTextView.setText(String.valueOf(adPackagesList.get(position).getPrice()) + " " + preferenceHelper.getCurrencySymbol());

        if (cateId.equals("2")) {
            holder.imagesNumberTextView.setVisibility(View.GONE);
            holder.imageIconImageView.setVisibility(View.GONE);
        }
        holder.numberSecoundsTextView.setText(adPackagesList.get(position).getSeconds() + " " + resources.getString(R.string.Category_Packages_activity_Seconds));

        if (cateId.equals("4")) {
            holder.packagesConstraintLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, PaymentOptionsActivity.class);
                intent.putExtra(CATEGORY_ID, cateId);
                intent.putExtra(PACKAGE_TIME_LONG_ID, adPackagesList.get(position).getSeconds());
                intent.putExtra(AD_PACKAGE_ID, adPackagesList.get(position));
                context.startActivity(intent);
            });
        } else {
            holder.packagesConstraintLayout.setOnClickListener(v -> {
                //ActivityHelper.goToActivity(context, CreateNewAdActivity.class,false);
                Intent intent = new Intent(context, CreateNewAdActivity.class);
                intent.putExtra(CATEGORY_ID, cateId);
                intent.putExtra(AD_PACKAGE_ID, adPackagesList.get(position));
                /*intent.putExtra(PACKAGE_TIME_LONG_ID, adPackagesList.get(position).getSeconds());
                intent.putExtra(PACKAGE_PRICE, String.valueOf(adPackagesList.get(position).getPrice()));
                intent.putExtra(PACKAGE_ID, String.valueOf(adPackagesList.get(position).getId()));
                intent.putExtra(PACKAGE_REACH_NUMBER_ID, adPackagesList.get(position).getReach());
                intent.putExtra(NUMBER_OF_IMAGES_ID, String.valueOf(adPackagesList.get(position).getNumberOfImages()));*/
                context.startActivity(intent);
            });
        }

        //}
    }

    @Override
    public int getItemCount() {
        return adPackagesList.size() /*+ 1*/;
    }

    class PackagesHolder extends RecyclerView.ViewHolder {
        private TextView reachNumberTextView, imagesNumberTextView, priceTextView, numberSecoundsTextView;
        private ConstraintLayout packagesConstraintLayout;
        private ImageView imageIconImageView, customPackageImageView;
        private LinearLayout reachNumberLinearLayout, imagesNumberLinearLayout, numberSecoundsLinearLayout;

        public PackagesHolder(@NonNull View itemView) {
            super(itemView);
            reachNumberTextView = itemView.findViewById(R.id.number_reach_textView_id);
            imagesNumberTextView = itemView.findViewById(R.id.number_images_textView_id);
            priceTextView = itemView.findViewById(R.id.package_price_textView_id);
            numberSecoundsTextView = itemView.findViewById(R.id.number_secounds_textView_id);
            packagesConstraintLayout = itemView.findViewById(R.id.packages_ConstraintLayout_id);
            //icons
            imageIconImageView = itemView.findViewById(R.id.image_icon_Image_view_id);
            customPackageImageView = itemView.findViewById(R.id.custom_package_imageView_id);
            reachNumberLinearLayout = itemView.findViewById(R.id.number_reach_LinearLayout_id);
            imagesNumberLinearLayout = itemView.findViewById(R.id.number_images_LinearLayout_id);
            numberSecoundsLinearLayout = itemView.findViewById(R.id.number_secounds_LinearLayout_id);

        }
    }
}
