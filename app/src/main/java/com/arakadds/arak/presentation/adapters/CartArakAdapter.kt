package com.arakadds.arak.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.data.ArakProduct
import com.arakadds.arak.databinding.CartItemProductArakBinding
import com.arakadds.arak.model.arakStoreModel.StoreProductData
import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.bumptech.glide.Glide


class CartArakAdapter(
    context: Context,
    private val productList: List<ArakProduct>,
    val currancy: String,
    cartActivity: CartActivity,
) :
    RecyclerView.Adapter<CartArakAdapter.ProductViewHolder>() {
    private val context: Context = context
    val cartActivity: CartActivity = cartActivity
    //var quantity = 0

   // private var totalPrice: Double = 0.0


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Use View Binding to inflate the layout
        val binding: CartItemProductArakBinding =
            CartItemProductArakBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(binding)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(@NonNull holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        val quantity = product.quantity ?: 0
        val pricePerUnit = product.price.toFloat()

        // Update UI
        Glide.with(context).load(product.image.toString())
            .placeholder(R.drawable.image_holder_virtical)
            .into(holder.binding.itemImageView)

        holder.binding.itemName.text = product.productName
        holder.binding.itemPrice.text = "${pricePerUnit * quantity} $currancy"
        holder.binding.itemQuantity.text = quantity.toString()

        // Calculate and update total price
        updateTotalPrice()

        holder.binding.buttonMinus.setOnClickListener {
            if (quantity > 1) {
                product.quantity = quantity - 1
                notifyItemChanged(position)
                cartActivity.onDecreaseProductQuantity(product)
                updateTotalPrice()
            } else if (quantity == 1) {
                product.quantity = 0
                cartActivity.onDecreaseProductQuantity(product)
                cartActivity.onRemoveProduct(position)
                updateTotalPrice()
            }
        }

        holder.binding.buttonPlus.setOnClickListener {
            product.quantity = quantity + 1
            notifyItemChanged(position)
            cartActivity.onAddProduct(product)
            updateTotalPrice()
        }
    }

    private fun updateTotalPrice() {
        val total = productList.sumOf { product ->
            product.price.toDouble() * (product.quantity ?: 0)
        }
        cartActivity.onTotalPriceUpdated(productList.size, total)
    }


    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(binding: CartItemProductArakBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        // Use View Binding
        var binding: CartItemProductArakBinding = binding
    }

    companion object {
        var productDetails: StoreProductData? = null
    }

}
