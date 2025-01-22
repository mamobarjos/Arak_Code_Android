package com.arakadds.arak.model.new_mapping_refactore.response.banners.notification

import com.google.gson.annotations.SerializedName

data class NotificationsData(

    @SerializedName("notifications") val notifications: ArrayList<NotificationObject>,
    @SerializedName("notification_count") val notificationCount: String

)
