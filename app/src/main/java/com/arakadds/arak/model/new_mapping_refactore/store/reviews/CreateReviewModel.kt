package com.arakadds.arak.model.new_mapping_refactore.store.reviews

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview
import com.google.gson.annotations.SerializedName

data class CreateReviewModel(

    @SerializedName("data")
    var storeProductReview: StoreProductReview

):BaseResponse()
