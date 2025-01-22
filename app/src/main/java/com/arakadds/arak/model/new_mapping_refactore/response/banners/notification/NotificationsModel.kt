package com.arakadds.arak.model.new_mapping_refactore.response.banners.notification

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class NotificationsModel(

    @SerializedName("data") val data: NotificationsDataWrapper,

    ):BaseResponse()
