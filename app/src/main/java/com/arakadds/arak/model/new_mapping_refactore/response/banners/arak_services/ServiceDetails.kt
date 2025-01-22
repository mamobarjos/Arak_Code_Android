package com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services

import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("title_ar") val titleAr: String,
    @SerializedName("title_en") val titleEn: String,
    @SerializedName("short_desc_ar") val shortDescAr: String,
    @SerializedName("short_desc_en") val shortDescEn: String,
    @SerializedName("long_desc_ar") val longDescAr: String,
    @SerializedName("long_desc_en") val longDescEn: String,
    @SerializedName("img_url") val imgUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?
)
