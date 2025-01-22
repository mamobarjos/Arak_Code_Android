package com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class ArakServicesModel(
    @SerializedName("data")
    var servicesData:ServicesData
):BaseResponse()
