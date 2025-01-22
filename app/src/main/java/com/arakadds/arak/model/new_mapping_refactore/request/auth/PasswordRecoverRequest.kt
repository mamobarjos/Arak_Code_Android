package com.arakadds.arak.model.new_mapping_refactore.request.auth

import com.google.gson.annotations.SerializedName

data class ForgetPasswordRequest(

    @SerializedName("phone_no") var phoneNumber: String,
    @SerializedName("email") var email: String,
    @SerializedName("otp_code") var otpCode: String,
    @SerializedName("new_password") var newPassword: String,
)
