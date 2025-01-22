package com.arakadds.arak.model.new_mapping_refactore.response.banners.other

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class AboutModel(
    @SerializedName("data") val data: ContentData,
):BaseResponse()
