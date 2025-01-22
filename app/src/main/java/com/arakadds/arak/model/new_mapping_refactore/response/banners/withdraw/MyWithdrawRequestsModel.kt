package com.arakadds.arak.model.new_mapping_refactore.response.banners.withdraw

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class MyWithdrawRequestsModel(
    @SerializedName("data") val data: WithdrawRequestsData,
):BaseResponse()
