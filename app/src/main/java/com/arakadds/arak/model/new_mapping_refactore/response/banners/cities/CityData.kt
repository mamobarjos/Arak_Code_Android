package com.arakadds.arak.model.new_mapping_refactore.response.banners.cities

import com.google.gson.annotations.SerializedName

data class CityData(
    @SerializedName("cities") val citiesArrayList: ArrayList<CityResponseData>,
)
