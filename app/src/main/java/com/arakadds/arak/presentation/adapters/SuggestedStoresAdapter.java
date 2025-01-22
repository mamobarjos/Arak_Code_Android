package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.MethodHelper;
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class SuggestedStoresAdapter extends RecyclerView.Adapter<SuggestedStoresAdapter.StoresHolder> {
    private static final String TAG = "UserTransactionsAdapter";

    private ArrayList<StoreObject> storesListArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private SuggestedStoresClickEvents storeClickEvents;

    public SuggestedStoresAdapter(ArrayList<StoreObject> storesListArrayList, Context context, String language, Resources resources, SuggestedStoresClickEvents storeClickEvents) {
        this.context = context;
        this.storesListArrayList = storesListArrayList;
        this.language = language;
        this.resources = resources;
        this.storeClickEvents = storeClickEvents;
    }

    public interface SuggestedStoresClickEvents {
        void onStoreClickedCalledBack(int position, int storeId);

        void onNextPageRequired();
    }

    @NonNull
    @Override
    public StoresHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_store_list_item, parent, false);
        return new StoresHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull StoresHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_holder_virtical);

        holder.storeNameTextView.setText(storesListArrayList.get(position).getName());
        holder.storeDescTextView.setText(storesListArrayList.get(position).getDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.storeDateTextView.setText(MethodHelper.INSTANCE.formatDate(storesListArrayList.get(position).getCreatedAt()));
        } else {
            holder.storeDateTextView.setText(storesListArrayList.get(position).getCreatedAt());
        }
        try {
            if (language.equals("en")) {
                holder.storeCategoryTextView.setText(storesListArrayList.get(position).getStoreCategory().getNameEn());
            } else {
                holder.storeCategoryTextView.setText(storesListArrayList.get(position).getStoreCategory().getNameAr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Glide.with(context).load(storesListArrayList.get(position).getImgUrl()).apply(options).into(holder.storeProfileImageView);
       /* holder.storeRatingBar.setRating(storesListArrayList.get(position).getRating());
        holder.storeRateTextView.setText(storesListArrayList.get(position).getRating().toString());*/
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
        private ImageView storeProfileImageView;
        private TextView storeNameTextView, storeCategoryTextView, storeDescTextView, storeDateTextView;
        private ConstraintLayout storeConstraintLayout;

        public StoresHolder(@NonNull View itemView) {
            super(itemView);
            storeProfileImageView = itemView.findViewById(R.id.store_image_ImageView_id);
            storeCategoryTextView = itemView.findViewById(R.id.store_category_textView_id);
            storeNameTextView = itemView.findViewById(R.id.store_name_textView_id);
            storeDescTextView = itemView.findViewById(R.id.store_desc_textView_id);
            storeDateTextView = itemView.findViewById(R.id.store_date_textView_id);
            storeConstraintLayout = itemView.findViewById(R.id.store_constraintLayout_id);

        }
    }
}
