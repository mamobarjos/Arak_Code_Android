package com.arakadds.arak.model.new_mapping_refactore.request.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SocialLoginRequest(
    @SerializedName("idToken") val idToken: String?,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("city_id") val cityId: Int,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("otp_code") val otpCode: String?
) : Serializable


