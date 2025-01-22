package com.arakadds.arak.model.new_mapping_refactore.store.products

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class RelatedStoreProductsModel(

    @SerializedName("data") val productArrayList: ArrayList<Product>


):BaseResponse()
