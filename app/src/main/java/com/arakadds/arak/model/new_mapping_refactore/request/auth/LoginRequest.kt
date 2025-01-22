package com.arakadds.arak.model.new_mapping_refactore.request.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginRequest(
    @SerializedName("phone_no")
    var phoneNumber: String,
    @SerializedName("password")
    var password: String
) : Serializable


