package com.arakadds.arak.model.new_mapping_refactore.store.categories

import com.google.gson.annotations.SerializedName
import io.paperdb.Paper

data class StoreCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("icon_url") val iconUrl: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?
){
    override fun toString(): String {
        val language = Paper.book().read<String>("language")
        return if (language.equals("ar")){
            nameAr;
        }else {
            nameEn;
        }
    }
}
