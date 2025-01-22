package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.ActivityHelper;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category.AdsCategoryData;
import com.arakadds.arak.presentation.activities.ads.CategoryPackagesActivity;

import java.util.ArrayList;

import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.STORES;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO;
import static com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE;
import static com.arakadds.arak.utils.Constants.CATEGORY_ID;

public class NewAdsAdapter extends RecyclerView.Adapter<NewAdsAdapter.CategoryHolder> {
    private ArrayList<AdsCategoryData> adsCategoryDataArrayList;
    private Context context;
    private String language;

    public NewAdsAdapter(ArrayList<AdsCategoryData> adsCategoryDataArrayList, Context context, String language) {
        this.adsCategoryDataArrayList = adsCategoryDataArrayList;
        this.context = context;
        this.language = language;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_categories, parent, false);

        return new NewAdsAdapter.CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, @SuppressLint("RecyclerView") int position) {

        if (language.equals("ar")) {
            holder.CategoryNameTextView.setText(adsCategoryDataArrayList.get(position).getNameAr());
        } else {
            holder.CategoryNameTextView.setText(adsCategoryDataArrayList.get(position).getNameEn());
        }

        switch (adsCategoryDataArrayList.get(position).getId()) {
            case IMAGE:
                holder.categoryIconImageView.setImageResource(R.drawable.ads_image_icon);
                break;
            case VIDEO:
                holder.categoryIconImageView.setImageResource(R.drawable.video_icon);
                break;
            case WEBSITE:
                holder.categoryIconImageView.setImageResource(R.drawable.adds_website_icon);
                break;
            case STORES:
                holder.categoryIconImageView.setImageResource(R.drawable.store_ad_icon);
                break;
            default:
                holder.categoryIconImageView.setImageResource(R.drawable.small_logo);
        }


        holder.categoriesConstraintLayout.setOnClickListener(v -> ActivityHelper.goToActivity(context, CategoryPackagesActivity.class, false, CATEGORY_ID,
                String.valueOf(adsCategoryDataArrayList.get(position).getId())));
    }

    @Override
    public int getItemCount() {
        return adsCategoryDataArrayList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        private ImageView categoryIconImageView;
        private TextView CategoryNameTextView;
        private ConstraintLayout categoriesConstraintLayout;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryIconImageView = itemView.findViewById(R.id.category_imageView_id);
            CategoryNameTextView = itemView.findViewById(R.id.category_title_textView_id);
            categoriesConstraintLayout = itemView.findViewById(R.id.categories_ConstraintLayout_id);
        }
    }
}
