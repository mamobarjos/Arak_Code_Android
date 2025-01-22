package com.arakadds.arak.model.new_mapping_refactore.request

import com.google.gson.annotations.SerializedName

data class StoreProductFileRequest(
    @SerializedName("url") val url: String
)
