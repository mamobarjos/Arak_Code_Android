package com.arakadds.arak.model.new_mapping_refactore.home

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class HomeAdsData(
    @SerializedName("ads")
    var adsDataArrayList:ArrayList<AdsData>

) : Pagination()
