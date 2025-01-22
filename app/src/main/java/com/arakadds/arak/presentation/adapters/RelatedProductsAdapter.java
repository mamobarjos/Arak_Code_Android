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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.preferaence.IPreferenceHelper;
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile;
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RelatedProductsAdapter extends RecyclerView.Adapter<RelatedProductsAdapter.StoreProductsHolder> {

    private static final String TAG = "StoreProductsAdapter";

    private ArrayList<Product> productArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private RelatedProductClickEvents relatedProductClickEvents;
    private IPreferenceHelper preferenceHelper;
    private String storeName;

    public RelatedProductsAdapter(ArrayList<Product> productArrayList, Context context
            , String language, Resources resources, IPreferenceHelper preferenceHelper, RelatedProductClickEvents relatedProductClickEvents) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.language = language;
        this.resources = resources;
        this.relatedProductClickEvents = relatedProductClickEvents;
        this.preferenceHelper = preferenceHelper;
        this.storeName = storeName;
    }

    public interface RelatedProductClickEvents {
        void onRelatedProductClickedCalledBack(int position, Product product);
    }

    @NonNull
    @Override
    public StoreProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_related_products, parent, false);
        return new StoreProductsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreProductsHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_placeholder);
        try {
            Glide.with(context).load(productArrayList.get(position).getStoreProductFiles().get(0).getUrl()).apply(options).into(holder.productImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.storeNameTextView.setText(productArrayList.get(position).getStore().getName());
        holder.productNameTextView.setText(productArrayList.get(position).getName());


        try {
            if (productArrayList.get(position).getSalePrice() != null && Double.parseDouble(productArrayList.get(position).getSalePrice()) > 0.0) {
                holder.productPriceTextView.setText(productArrayList.get(position).getSalePrice() + " " + preferenceHelper.getCurrencySymbol());
                holder.productOldPriceTextView.setText(productArrayList.get(position).getPrice() + " " + preferenceHelper.getCurrencySymbol());
            } else {
                holder.productPriceTextView.setText(productArrayList.get(position).getPrice() + " " + preferenceHelper.getCurrencySymbol());
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.productPriceTextView.setText(productArrayList.get(position).getPrice() + " " + preferenceHelper.getCurrencySymbol());
        }

        holder.productRatingTextView.setText(productArrayList.get(position).getRating());
        if (productArrayList.get(position).getRating() != null) {
            holder.productratingBar.setRating(Float.parseFloat(productArrayList.get(position).getRating()));
        }
        holder.productConstraintLayout.setOnClickListener(v -> relatedProductClickEvents.onRelatedProductClickedCalledBack(position
                , productArrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        if (productArrayList != null) {
            return productArrayList.size();
        }
        return 0;
    }

    class StoreProductsHolder extends RecyclerView.ViewHolder {

        private ImageView productImageView;
        private TextView productNameTextView, storeNameTextView, productPriceTextView, productRatingTextView, productOldPriceTextView;
        private ConstraintLayout productConstraintLayout;
        private MaterialRatingBar productratingBar;

        public StoreProductsHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.related_product_image_imageView_id);
            productNameTextView = itemView.findViewById(R.id.related_product_name_textView_id);
            storeNameTextView = itemView.findViewById(R.id.related_product_shope_name_textView_id);
            productPriceTextView = itemView.findViewById(R.id.related_product_price_textView_id);
            productOldPriceTextView = itemView.findViewById(R.id.related_product_old_price_textView_id);
            productConstraintLayout = itemView.findViewById(R.id.related_product_ConstraintLayout_id);
            productratingBar = itemView.findViewById(R.id.product_ratingBar_id);
            productRatingTextView = itemView.findViewById(R.id.product_rate_text_textView_id);

        }
    }
}
