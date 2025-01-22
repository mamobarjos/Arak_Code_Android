package com.arakadds.arak.model.new_mapping_refactore.response.banners.notification

import com.google.gson.annotations.SerializedName
data class NotificationObject(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String?,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("sender_id") val senderId: Int?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?
)