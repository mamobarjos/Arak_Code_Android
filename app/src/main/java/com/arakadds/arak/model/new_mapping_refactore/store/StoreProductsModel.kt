package com.arakadds.arak.model.new_mapping_refactore.store

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.products.ProductData
import com.google.gson.annotations.SerializedName

data class StoreProductsModel(

    @SerializedName("data") val data: ProductData?,


    ):BaseResponse()
