package com.arakadds.arak.model.new_mapping_refactore.response.banners.auth

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class RegistrationModel(
    @SerializedName("data") val data: UserRegistrationObject,
) : BaseResponse()
