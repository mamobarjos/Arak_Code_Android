package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoresHolder> {
    private static final String TAG = "UserTransactionsAdapter";

    private ArrayList<StoreObject> storesListArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private StoreClickEvents storeClickEvents;

    public StoresAdapter(ArrayList<StoreObject> storesListArrayList, Context context, String language, Resources resources, StoreClickEvents storeClickEvents) {
        this.context = context;
        this.storesListArrayList = storesListArrayList;
        this.language = language;
        this.resources = resources;
        this.storeClickEvents = storeClickEvents;
    }
    public interface StoreClickEvents {
        void onStoreClickedCalledBack(int position, int storeId);
        void onNextPageRequired();
    }
    @NonNull
    @Override
    public StoresHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item_row, parent, false);
        return new StoresHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull StoresHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_holder_virtical);

        holder.storeNameTextView.setText(storesListArrayList.get(position).getName());
        try {
            if (language.equals("en")){
                holder.storeCategoryTextView.setText(storesListArrayList.get(position).getStoreCategory().getNameEn());
            }else {
                holder.storeCategoryTextView.setText(storesListArrayList.get(position).getStoreCategory().getNameAr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Glide.with(context).load(storesListArrayList.get(position).getImgUrl()).apply(options).into(holder.storeProfileImageView);
        Glide.with(context).load(storesListArrayList.get(position).getCoverImgUrl()).apply(options).into(holder.storeCoverImageView);
        if (storesListArrayList.get(position).getRating()!=null){

            float ratingValue = storesListArrayList.get(position).getRating() != null ? storesListArrayList.get(position).getRating() : 0.0f; // Default to 0 if null
            holder.storeRatingBar.setRating(ratingValue);

            //holder.storeRatingBar.setRating(storesListArrayList.get(position).getRating());
            holder.storeRateTextView.setText(storesListArrayList.get(position).getRating().toString());
        }
        holder.storeConstraintLayout.setOnClickListener(v -> storeClickEvents.onStoreClickedCalledBack(position, storesListArrayList.get(position).getId()));
        if (storesListArrayList.size() - 1 == position) {
            storeClickEvents.onNextPageRequired();
        }
    }

    @Override
    public int getItemCount() {
        if (storesListArrayList != null) {
            return storesListArrayList.size();
        }
        return 0;
    }

    class StoresHolder extends RecyclerView.ViewHolder {

        private ImageView storeCoverImageView, storeProfileImageView;
        private TextView storeNameTextView, storeCategoryTextView, storeRateTextView;
        private MaterialRatingBar storeRatingBar;
        private ConstraintLayout storeConstraintLayout;

        public StoresHolder(@NonNull View itemView) {
            super(itemView);
            storeCoverImageView = itemView.findViewById(R.id.stores_list_cover_image_imageView_id);
            storeProfileImageView = itemView.findViewById(R.id.stores_list_profile_image_imageView_id);
            storeNameTextView = itemView.findViewById(R.id.stores_list_name_textView_id);
            storeCategoryTextView = itemView.findViewById(R.id.stores_list_category_textView_id);
            storeRatingBar = itemView.findViewById(R.id.stores_list_ratingBar_id);
            storeRateTextView = itemView.findViewById(R.id.stores_list_rate_TextView_id);
            storeConstraintLayout = itemView.findViewById(R.id.stores_list_ConstraintLayout_id);
        }
    }
}
