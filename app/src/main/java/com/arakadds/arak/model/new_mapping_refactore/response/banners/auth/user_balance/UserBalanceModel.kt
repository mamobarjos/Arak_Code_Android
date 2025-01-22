package com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class UserBalanceModel(
    @SerializedName("data")
    var userBalanceData:UserBalanceData
):BaseResponse()
