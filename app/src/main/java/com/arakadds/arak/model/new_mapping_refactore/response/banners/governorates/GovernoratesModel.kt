package com.arakadds.arak.model.new_mapping_refactore.response.banners.governorates

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class GovernoratesModel(

    @SerializedName("data") val governoratesData: GovernoratesData,

    ):BaseResponse()
