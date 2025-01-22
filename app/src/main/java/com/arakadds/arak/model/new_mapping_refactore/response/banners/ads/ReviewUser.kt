package com.arakadds.arak.model.new_mapping_refactore.response.banners.ads

import com.google.gson.annotations.SerializedName

data class ReviewUser(
    @SerializedName("id") val id: Int,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("img_avatar") val imgAvatar: String
)
