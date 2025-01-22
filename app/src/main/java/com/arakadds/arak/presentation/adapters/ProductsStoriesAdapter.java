package com.arakadds.arak.presentation.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ProductsStoriesAdapter extends RecyclerView.Adapter<ProductsStoriesAdapter.ProductStoryHolder> {

    private ArrayList<Product> storeProductsResponseArrayList;
    private ProductClickEventCallBack productClickEventCallBack;
    private Activity activity;

    public ProductsStoriesAdapter(ArrayList<Product> storeProductsResponseArrayList, ProductClickEventCallBack productClickEventCallBack, Activity activity) {
        this.storeProductsResponseArrayList = storeProductsResponseArrayList;
        this.productClickEventCallBack = productClickEventCallBack;
        this.activity = activity;
    }

    public interface ProductClickEventCallBack {
        void onProductSelectedCallBack(int productId);
    }

    @NonNull
    @Override
    public ProductStoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_products_stories, parent, false);
        return new ProductStoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductStoryHolder viewHolder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_holder_virtical);
        if (storeProductsResponseArrayList.get(position).getStoreProductFiles().size() > 0) {
            Glide.with(activity).load(storeProductsResponseArrayList.get(position).getStoreProductFiles().get(0).getUrl())
                    .apply(options).into(viewHolder.productStoryImageView);
        }
        viewHolder.productStoryImageView.setOnClickListener(view -> productClickEventCallBack.onProductSelectedCallBack(storeProductsResponseArrayList.get(position).getId()));

        viewHolder.productStoryTitleTextView.setText(storeProductsResponseArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (storeProductsResponseArrayList != null) {
            return storeProductsResponseArrayList.size();
        }
        return 0;
    }

    public class ProductStoryHolder extends RecyclerView.ViewHolder {

        ImageView productStoryImageView;
        TextView productStoryTitleTextView;

        public ProductStoryHolder(View itemView) {
            super(itemView);
            productStoryImageView = itemView.findViewById(R.id.product_story_imageView_id);
            productStoryTitleTextView = itemView.findViewById(R.id.product_story_title_textView_id);
        }
    }

}