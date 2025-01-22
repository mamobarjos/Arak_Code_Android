package com.arakadds.arak.model.new_mapping_refactore.response.banners.countries

import com.google.gson.annotations.SerializedName
import io.paperdb.Paper

data class CountriesResponseData(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("currency_id") val currencyId: Int,
    @SerializedName("email_required") val isEmailRequired: Boolean = false,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("currency") val currency: Currency
) {
    override fun toString(): String {
        val language = Paper.book().read<String>("language")
        return if (language.equals("ar")) {
            nameAr;
        } else {
            nameEn;
        }
    }
}