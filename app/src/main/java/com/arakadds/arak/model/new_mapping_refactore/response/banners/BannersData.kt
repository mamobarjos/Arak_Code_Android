package com.arakadds.arak.model.new_mapping_refactore.response.banners

import com.google.gson.annotations.SerializedName

data class BannersData(
    @SerializedName("banners") val bannersArrayList: ArrayList<BannerObject>,

)
