package com.arakadds.arak.model.new_mapping_refactore.store

import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.google.gson.annotations.SerializedName

data class StoreProductsData(
    @SerializedName("products")
    var storeProductsResponseArrayList: ArrayList<Product>
)
