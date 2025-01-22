package com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info

import com.google.gson.annotations.SerializedName

data class UserCity(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("country_id") val countryId: Int
)
