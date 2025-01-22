package com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category

import com.google.gson.annotations.SerializedName

data class AdsCategoryResponse(

    @SerializedName("adCategories")
    var adsCategoryDataArrayList: ArrayList<AdsCategoryData>
)
