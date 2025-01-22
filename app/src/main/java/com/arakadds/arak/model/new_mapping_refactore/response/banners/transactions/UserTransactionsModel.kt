package com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class UserTransactionsModel(

    @SerializedName("data") val data: TransactionData,

    ):BaseResponse()
