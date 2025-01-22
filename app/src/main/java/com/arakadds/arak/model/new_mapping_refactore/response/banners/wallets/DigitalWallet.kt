package com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets

import com.arakadds.arak.model.new_mapping_refactore.home.Country
import com.google.gson.annotations.SerializedName
import io.paperdb.Paper

data class DigitalWallet(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("country") val country: Country
){

    override fun toString(): String {
        val language = Paper.book().read<String>("language")
        return if (language.equals("ar")) {
            nameAr
        } else {
            nameEn
        }
    }
}
