package com.arakadds.arak.model.new_mapping_refactore.home

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class HomeAdsModel(

    @SerializedName("data")
    var homeAdsData:HomeAdsData

) : BaseResponse()
