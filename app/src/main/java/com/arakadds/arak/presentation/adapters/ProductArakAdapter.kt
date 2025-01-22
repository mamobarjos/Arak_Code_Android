package com.arakadds.arak.presentation.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.databinding.ItemProductArakBinding
import com.arakadds.arak.model.arakStoreModel.StoreProductData
import com.arakadds.arak.presentation.activities.ArakStore.ProductDetailsToCartActivity
import com.arakadds.arak.presentation.adapters.NotificationsAdapter.NotificationsCallBacks
import com.bumptech.glide.Glide


class ProductArakAdapter(
    context: Context,
    private val productList: ArrayList<StoreProductData>,
    private val currentPage: Int,
    private val lastPage: Int,
    private val currancy: String,
    productCallBacks: ProductCallBacks
) :
    RecyclerView.Adapter<ProductArakAdapter.ProductViewHolder>() {
    private val context: Context = context
    private val productCallBacks: ProductCallBacks? = productCallBacks

    interface ProductCallBacks {
        fun onNextPageRequired()
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Use View Binding to inflate the layout
        val binding: ItemProductArakBinding =
            ItemProductArakBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        // Bind data to the views using binding
        Glide.with(context).load(product.images.get(0).src.toString())
            .placeholder(R.drawable.image_holder_virtical)
            .into(holder.binding.productImage)

        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = product.price + " " + currancy
        holder.binding.productRatingTextView.text = product.averageRating
        try {
            holder.binding.productRating.rating = product.averageRating!!.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.binding.productCard.setOnClickListener {
            productDetails = product
            context.startActivity(Intent(context, ProductDetailsToCartActivity::class.java))
        }

        if (lastPage > currentPage && productList.size - 1 == position) {
            productCallBacks?.onNextPageRequired()
        }
    }


    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(binding: ItemProductArakBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        // Use View Binding
        var binding: ItemProductArakBinding = binding
    }

    companion object {
        var productDetails: StoreProductData? = null
    }

}
