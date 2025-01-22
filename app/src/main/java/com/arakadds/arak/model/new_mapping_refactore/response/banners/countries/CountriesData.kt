package com.arakadds.arak.model.new_mapping_refactore.response.banners.countries

import com.google.gson.annotations.SerializedName

data class CountriesData(
    @SerializedName("countries")
    var countriesResponseDataArrayList:ArrayList<CountriesResponseData>
)
