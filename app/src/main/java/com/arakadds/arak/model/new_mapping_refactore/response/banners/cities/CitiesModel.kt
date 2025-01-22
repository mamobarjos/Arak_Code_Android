package com.arakadds.arak.model.new_mapping_refactore.response.banners.cities

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class CitiesModel(
    @SerializedName("data") val cityData: CityData,
) : BaseResponse()
