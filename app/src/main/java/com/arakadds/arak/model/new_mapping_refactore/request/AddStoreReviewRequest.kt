package com.arakadds.arak.model.new_mapping_refactore.request

import com.google.gson.annotations.SerializedName

data class AddStoreReviewRequest(
    @SerializedName("content")
    var content: String,
    @SerializedName("rating")
    var rating: Float,
    @SerializedName("store_id")
    var storeId: Int,
)

data class AddStoreProductReviewRequest(
    @SerializedName("content")
    var content: String,
    @SerializedName("rating")
    var rating: Float,
    @SerializedName("store_product_id")
    var storeProductId: Int,
)

data class AddAdReviewRequest(
    @SerializedName("content")
    var content: String,
    @SerializedName("rating")
    var rating: Float,
    @SerializedName("ad_id")
    var adId: Int,
)
