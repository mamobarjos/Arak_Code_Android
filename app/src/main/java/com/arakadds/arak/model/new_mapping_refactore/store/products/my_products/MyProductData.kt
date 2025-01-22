package com.arakadds.arak.model.new_mapping_refactore.store.products.my_products

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.google.gson.annotations.SerializedName

data class MyProductData(
    @SerializedName("storeProducts") val products: ArrayList<Product>,
) : Pagination()