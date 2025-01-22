package com.arakadds.arak.model.new_mapping_refactore.response.banners.other

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview
import com.google.gson.annotations.SerializedName

data class FeedbackModel(
    @SerializedName("data")
    var storeProductReview: StoreProductReview
) : BaseResponse()
