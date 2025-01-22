package com.arakadds.arak.model.new_mapping_refactore.response.banners.ranking

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class UsersRankingModel(
    @SerializedName("data") val data: UsersRankingData
):BaseResponse()
