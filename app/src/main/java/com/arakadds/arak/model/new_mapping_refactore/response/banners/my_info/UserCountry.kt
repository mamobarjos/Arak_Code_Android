package com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info

import com.google.gson.annotations.SerializedName

data class UserCountry(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("currency_id") val currencyId: Int,
    @SerializedName("currency") val currency: UserCurrency
)
