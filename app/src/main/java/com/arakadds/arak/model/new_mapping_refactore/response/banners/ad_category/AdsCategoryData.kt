package com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category

import com.google.gson.annotations.SerializedName

data class AdsCategoryData(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name_en")
    var nameEn: String,

    @SerializedName("name_ar")
    var nameAr: String,
)
