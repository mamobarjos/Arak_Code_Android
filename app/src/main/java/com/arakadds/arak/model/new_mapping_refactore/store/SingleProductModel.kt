package com.arakadds.arak.model.new_mapping_refactore.store

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class SingleProductModel(
    @SerializedName("data") val singleProductDataModel: SingleProductDataModel,
):BaseResponse()
