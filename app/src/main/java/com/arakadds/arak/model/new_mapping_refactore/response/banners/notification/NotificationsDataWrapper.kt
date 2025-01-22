package com.arakadds.arak.model.new_mapping_refactore.response.banners.notification

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class NotificationsDataWrapper(

    @SerializedName("data") val data: NotificationsData

):Pagination()
