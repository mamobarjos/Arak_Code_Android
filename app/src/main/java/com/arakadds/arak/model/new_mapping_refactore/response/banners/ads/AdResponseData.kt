package com.arakadds.arak.model.new_mapping_refactore.response.banners.ads

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.google.gson.annotations.SerializedName

data class AdResponseData(
   @SerializedName("data")
    var adData: AdsData
):BaseResponse()
