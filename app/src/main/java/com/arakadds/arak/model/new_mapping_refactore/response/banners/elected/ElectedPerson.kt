package com.arakadds.arak.model.new_mapping_refactore.response.banners.elected

import com.google.gson.annotations.SerializedName

data class ElectedPerson(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("img") val img: String,
    @SerializedName("cover_img") val coverImg: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("cluster") val cluster: String,
    @SerializedName("cluster_ar") val clusterAr: String,
    @SerializedName("governorate_id") val governorateId: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("governorate") val governorate: Governorate?
){
    override fun toString(): String {
        return name
    }
}
