package com.arakadds.arak.model.new_mapping_refactore.response.banners

import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.UserObject
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.google.gson.annotations.SerializedName

data class BannerObject(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("img_url") val imgUrl: String,
    @SerializedName("website_url") val websiteUrl: String,
    @SerializedName("type") val type: String,
    @SerializedName("is_homepage") val isHomepage: Boolean,
    @SerializedName("status") val status: String,
    @SerializedName("store_id") val storeId: Int?,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("user") val user: UserObject?, // Assuming user is an object or null
    @SerializedName("store") val store: StoreObject? // Assuming store is an object or null
)
