package com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class UserInformationModel(
    @SerializedName("data") val data: UserInformationData,
):BaseResponse()
