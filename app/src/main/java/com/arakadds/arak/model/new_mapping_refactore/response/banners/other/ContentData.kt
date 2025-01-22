package com.arakadds.arak.model.new_mapping_refactore.response.banners.other

import com.google.gson.annotations.SerializedName

data class ContentData(
    @SerializedName("id") val id: Int,
    @SerializedName("about_en") val aboutEn: String?,
    @SerializedName("terms_en") val termsEn: String?,
    @SerializedName("privacy_en") val privacyEn: String?,
    @SerializedName("about_ar") val aboutAr: String?,
    @SerializedName("terms_ar") val termsAr: String?,
    @SerializedName("privacy_ar") val privacyAr: String?,
    @SerializedName("need_help_en") val needHelpEn: String?,
    @SerializedName("need_help_ar") val needHelpAr: String?,
    @SerializedName("facebook") val facebook: String?,
    @SerializedName("x") val x: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("linkedin") val linkedin: String?,
    @SerializedName("snapchat") val snapchat: String?,
    @SerializedName("tiktok") val tiktok: String?,
    @SerializedName("youtube") val youtube: String?,
    @SerializedName("show_store_contact_info") val showStoreContactInfo: Boolean?,
    @SerializedName("android_build_version") val androidBuildVersion: String?,
    @SerializedName("ios_build_version") val iosBuildVersion: String?,
    @SerializedName("view_balance_increase_rate") val viewBalanceIncreaseRate: Float?
)