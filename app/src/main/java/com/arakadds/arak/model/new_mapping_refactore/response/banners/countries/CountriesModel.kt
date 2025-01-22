package com.arakadds.arak.model.new_mapping_refactore.response.banners.countries

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class CountriesModel(
    @SerializedName("data")
    var countriesData: CountriesData
) : BaseResponse()
