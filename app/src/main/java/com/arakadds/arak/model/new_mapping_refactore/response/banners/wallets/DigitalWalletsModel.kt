package com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class DigitalWalletsModel(

    @SerializedName("data") val data: DigitalWalletsData,

    ):BaseResponse()
