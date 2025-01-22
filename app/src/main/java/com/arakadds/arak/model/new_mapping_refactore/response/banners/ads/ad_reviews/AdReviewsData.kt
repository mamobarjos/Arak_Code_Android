package com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews

import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview
import com.google.gson.annotations.SerializedName

data class AdReviewsData(

    @SerializedName("reviews") val storeProductReviewArrayList: ArrayList<AdReviews>
)
