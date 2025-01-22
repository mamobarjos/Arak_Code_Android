package com.arakadds.arak.model.new_mapping_refactore.store.products

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class ProductData(

    @SerializedName("products") val products: ArrayList<Product>?,

    ) : Pagination()
