package com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info

import com.google.gson.annotations.SerializedName

data class Preferences(
    @SerializedName("categories") val categories: List<Int>
)
