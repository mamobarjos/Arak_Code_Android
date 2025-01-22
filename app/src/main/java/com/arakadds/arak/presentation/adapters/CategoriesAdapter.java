package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.store.categories.StoreCategory;
import com.arakadds.arak.presentation.activities.home.fragments.StoreFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder> {

    private ArrayList<StoreCategory> categoriesListArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private CategorySelectedEvents categorySelectedEvents;
    private int row_index;

    public CategoriesAdapter(ArrayList<StoreCategory> categoriesListArrayList, Context context, String language, Resources resources, StoreFragment categorySelectedEvents) {
        this.context = context;
        this.categoriesListArrayList = categoriesListArrayList;
        this.language = language;
        this.resources = resources;
        this.categorySelectedEvents = categorySelectedEvents;
    }

    public interface CategorySelectedEvents {
        void onCategorySelectedCalledBack(int position, int categoryId);
    }

    @NonNull
    @Override
    public CategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_packages_filter_item_row, parent, false);
        return new CategoriesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesHolder holder, @SuppressLint("RecyclerView") int position) {
        if (language.equals("ar")) {
            holder.categoryNameTextView.setText(categoriesListArrayList.get(position).getNameAr());
        } else {
            holder.categoryNameTextView.setText(categoriesListArrayList.get(position).getNameEn());
        }
        if (categoriesListArrayList.get(position).getId() == -1) {
            holder.categoryImageView.setImageResource(R.drawable.all_category_icon);
        } else {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.pink)
                    .error(R.color.pink);

            Glide.with(context).load(categoriesListArrayList.get(position).getIconUrl())
                    .apply(options).into(holder.categoryImageView);
        }

        holder.constraintLayout.setOnClickListener(v -> {
            row_index = position;
            notifyDataSetChanged();
            categorySelectedEvents.onCategorySelectedCalledBack(position
                    , categoriesListArrayList.get(position).getId());
        });

        if (row_index == position) {
            holder.categoryImageView.setBorderColor(context.getResources().getColor(R.color.orange_500));
            holder.categoryImageView.setBorderWidth(4);

            /*holder.categoryNameTextView.setBackgroundResource(R.color.orange_500);
            holder.categoryNameTextView.setTextColor(Color.parseColor("#ffffff"));*/
        } else {
            //holder.categoryImageView.setBorderColor(context.getResources().getColor(R.color.orange_500));
            holder.categoryImageView.setBorderWidth(0);
            /*holder.categoryNameTextView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.categoryNameTextView.setTextColor(Color.parseColor("#000000"));*/
        }
    }

    @Override
    public int getItemCount() {
        if (categoriesListArrayList != null) {
            return categoriesListArrayList.size();
        }
        return 0;
    }

    class CategoriesHolder extends RecyclerView.ViewHolder {

        private TextView categoryNameTextView;
        private CircleImageView categoryImageView;
        private ConstraintLayout constraintLayout;

        public CategoriesHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameTextView = itemView.findViewById(R.id.filter_category_name_TextView_id);
            categoryImageView = itemView.findViewById(R.id.product_story_imageView_id);
            constraintLayout = itemView.findViewById(R.id.category_ConstraintLayout_id);
        }
    }
}
