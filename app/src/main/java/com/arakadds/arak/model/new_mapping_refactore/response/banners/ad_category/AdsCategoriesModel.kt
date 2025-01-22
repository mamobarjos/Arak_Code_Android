package com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class AdsCategoriesModel(
    @SerializedName("data")
    var adsCategoryResponse:AdsCategoryResponse
):BaseResponse()
