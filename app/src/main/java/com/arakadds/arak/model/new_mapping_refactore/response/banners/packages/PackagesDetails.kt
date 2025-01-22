package com.arakadds.arak.model.new_mapping_refactore.response.banners.packages

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class PackagesDetails(

    @SerializedName("data")
    var packagesData: PackagesData

) : BaseResponse()
