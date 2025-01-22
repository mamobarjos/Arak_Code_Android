package com.arakadds.arak.model.new_mapping_refactore.store.products.my_products

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.products.ProductData
import com.google.gson.annotations.SerializedName

data class MyStoreProductsModel(

    @SerializedName("data") val data: MyProductData,

    ): BaseResponse()

