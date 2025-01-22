package com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.google.gson.annotations.SerializedName

data class CreateNewAdModel(

    @SerializedName("data")
    var adData: AdsData
):BaseResponse()
