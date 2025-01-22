package com.arakadds.arak.model.new_mapping_refactore.response.banners

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class BannersModel(

    @SerializedName("data") val bannersData: BannersData,

    ) : BaseResponse()
