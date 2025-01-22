package com.arakadds.arak.model.new_mapping_refactore.response.banners.auth

import com.google.gson.annotations.SerializedName

data class LoginResponseBody(
    @SerializedName("access_token") var token: String,
    @SerializedName("user") var userObject: UserObject
)
