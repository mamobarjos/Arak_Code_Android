package com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class ServicesData(
    @SerializedName("services") val services: ArrayList<ServiceDetails>,
):Pagination()
