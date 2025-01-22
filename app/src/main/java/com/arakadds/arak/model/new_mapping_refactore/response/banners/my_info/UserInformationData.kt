package com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info

import com.google.gson.annotations.SerializedName

data class UserInformationData(
    @SerializedName("id") val id: Int,
    @SerializedName("fullname") val fullName: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("birthdate") val birthDate: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("city_id") val cityId: Int,
    @SerializedName("role") val role: String,
    @SerializedName("balance") val balance: String,
    @SerializedName("img_avatar") val imgAvatar: String?,
    @SerializedName("notifications_enabled") val notificationsEnabled: Boolean,
    @SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("has_store") val hasStore: Boolean,
    @SerializedName("has_wallet") val hasWallet: Boolean,
    @SerializedName("ads_imgs_views") val adsImgsViews: Int,
    @SerializedName("ads_videos_views") val adsVideosViews: Int,
    @SerializedName("ads_website_views") val adsWebsiteViews: Int,
    //@SerializedName("preferences") val preferences: Preferences,
    @SerializedName("oauthId") val oauthId: String?,
    @SerializedName("sl_provider") val slProvider: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("country") val country: UserCountry,
    @SerializedName("city") val city: UserCity
)
