package com.arakadds.arak.presentation.adapters;

import static com.arakadds.arak.utils.AppEnums.LanguagesEnums.ENGLISH;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.preferaence.IPreferenceHelper;
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class StoreProductsAdapter extends RecyclerView.Adapter<StoreProductsAdapter.StoreProductsHolder> {

    private static final String TAG = "StoreProductsAdapter";

    private ArrayList<Product> storeProductsResponseArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private ProductClickEvents productClickEvents;
    private String storeName;
    private boolean isMyProducts;
    private IPreferenceHelper preferenceHelper;
    private MyProductsCallBacks myProductsCallBacks;

    public StoreProductsAdapter(ArrayList<Product> storeProductsResponseArrayList, Context context
            , String language, Resources resources, ProductClickEvents productClickEvents, String storeName, boolean isMyProducts, IPreferenceHelper preferenceHelper, MyProductsCallBacks myProductsCallBacks) {
        this.context = context;
        this.storeProductsResponseArrayList = storeProductsResponseArrayList;
        this.language = language;
        this.resources = resources;
        this.productClickEvents = productClickEvents;
        this.storeName = storeName;
        this.isMyProducts = isMyProducts;
        this.preferenceHelper = preferenceHelper;
        this.myProductsCallBacks = myProductsCallBacks;
    }

    public interface MyProductsCallBacks {
        void onNextPageRequired();
    }

    public interface ProductClickEvents {
        void onProductClickedCalledBack(int position, int productId);

        void onEditProductClickedCalledBack(int position, Product product);

        void onViewProductClickedCalledBack(int position, int productId);

        void onRemoveProductClickedCalledBack(int position, int productId);
    }

    @NonNull
    @Override
    public StoreProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_store_product, parent, false);
        return new StoreProductsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreProductsHolder holder, int position) {
        //holder.editImageView.setVisibility(View.GONE);//todo remove this after connect edit product
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_placeholder);
        try {
            Glide.with(context).load(storeProductsResponseArrayList.get(position).getStoreProductFiles().get(0).getUrl()).apply(options).into(holder.productImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.productNameTextView.setText(storeProductsResponseArrayList.get(position).getName());

        try {
            holder.productDescTextView.setText(storeProductsResponseArrayList.get(position).getStore().getName());
        } catch (NullPointerException e) {
            if (language.equals(ENGLISH)) {
                holder.productDescTextView.setText(storeProductsResponseArrayList.get(position).getStore().getStoreCategory().getNameEn());
            } else {
                holder.productDescTextView.setText(storeProductsResponseArrayList.get(position).getStore().getStoreCategory().getNameAr());
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (storeProductsResponseArrayList.get(position).getSalePrice() != null && Double.parseDouble(storeProductsResponseArrayList.get(position).getSalePrice()) > 0.0) {
                holder.productNewPriceTextView.setText(storeProductsResponseArrayList.get(position).getSalePrice() + " " + preferenceHelper.getCurrencySymbol());
                holder.productOldPriceTextView.setText(storeProductsResponseArrayList.get(position).getPrice() + " " + preferenceHelper.getCurrencySymbol());
            } else {
                holder.productNewPriceTextView.setText(storeProductsResponseArrayList.get(position).getPrice() + " " + preferenceHelper.getCurrencySymbol());
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.productNewPriceTextView.setText(storeProductsResponseArrayList.get(position).getPrice() + " " + preferenceHelper.getCurrencySymbol());
        }

        holder.rateTextTextView.setText(storeProductsResponseArrayList.get(position).getRating());

        try {
            holder.productRatingBar.setRating(Float.parseFloat(storeProductsResponseArrayList.get(position).getRating()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  holder.editImageView.setOnClickListener(v -> productClickEvents.onEditProductClickedCalledBack(position, storeProductsResponseArrayList.get(position)));

        // holder.viewImageView.setOnClickListener(v -> productClickEvents.onViewProductClickedCalledBack(position, storeProductsResponseArrayList.get(position).getId()));

        // holder.removeImageView.setOnClickListener(v -> productClickEvents.onRemoveProductClickedCalledBack(position, storeProductsResponseArrayList.get(position).getId()));

        holder.productConstraintLayout.setOnClickListener(v -> {
            /*if (isMyProducts) {
                holder.editOptionsLinearLayout.setVisibility(View.VISIBLE);
            } else {
                productClickEvents.onProductClickedCalledBack(position
                        , storeProductsResponseArrayList.get(position).getId());
            }*/
            productClickEvents.onProductClickedCalledBack(position
                    , storeProductsResponseArrayList.get(position).getId());
        });

        if (storeProductsResponseArrayList.size() - 1 == position) {
            myProductsCallBacks.onNextPageRequired();
        }

    }

    @Override
    public int getItemCount() {
        if (storeProductsResponseArrayList != null) {
            return storeProductsResponseArrayList.size();
        }
        return 0;
    }

    class StoreProductsHolder extends RecyclerView.ViewHolder {

        private ImageView productImageView/*, editImageView, viewImageView, removeImageView*/;
        private TextView productNameTextView, productDescTextView, productNewPriceTextView, productOldPriceTextView, rateTextTextView;
        private MaterialRatingBar productRatingBar;
        private ConstraintLayout productConstraintLayout;
        //private LinearLayout editOptionsLinearLayout;

        public StoreProductsHolder(@NonNull View itemView) {
            super(itemView);

            /*editImageView = itemView.findViewById(R.id.product_edit_ImageView_id);
            viewImageView = itemView.findViewById(R.id.product_view_ImageView_id);
            removeImageView = itemView.findViewById(R.id.product_remove_ImageView_id);*/
            productImageView = itemView.findViewById(R.id.product_imageView_id);
            productNameTextView = itemView.findViewById(R.id.product_name_textView_id);
            productDescTextView = itemView.findViewById(R.id.product_desc_textView_id);
            productRatingBar = itemView.findViewById(R.id.product_ratingBar_id);
            rateTextTextView = itemView.findViewById(R.id.product_rate_text_textView_id);
            productNewPriceTextView = itemView.findViewById(R.id.product_new_price_textView_id);
            productOldPriceTextView = itemView.findViewById(R.id.product_old_price_textView_id);
            //editOptionsLinearLayout = itemView.findViewById(R.id.product_edit_options_LinearLayout_id);
            productConstraintLayout = itemView.findViewById(R.id.product_ConstraintLayout_id);
        }
    }
}
