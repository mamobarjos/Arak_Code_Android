package com.arakadds.arak.model.new_mapping_refactore.response.banners.countries

import com.google.gson.annotations.SerializedName

data class Currency(
    @SerializedName("id") val id: Int,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("exchange_rate") val exchangeRate: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?
)
