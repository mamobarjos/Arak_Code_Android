package com.arakadds.arak.model.new_mapping_refactore.request

import com.google.gson.annotations.SerializedName

class RequestArakServiceRequestBody(
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("phone_no")
    var phoneNumber: String,
    @SerializedName("service_id")
    var serviceId: Int?
)