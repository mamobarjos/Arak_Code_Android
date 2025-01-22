package com.arakadds.arak.model.new_mapping_refactore.response.banners.auth

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.LoginResponseBody
import com.google.gson.annotations.SerializedName

data class LoginResponseData(

    @SerializedName("data") var loginResponseBody: LoginResponseBody

) : BaseResponse()
