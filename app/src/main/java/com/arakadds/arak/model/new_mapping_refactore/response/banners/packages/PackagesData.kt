package com.arakadds.arak.model.new_mapping_refactore.response.banners.packages

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class PackagesData(
    @SerializedName("adPackages")
    var adPackagesArrayList:ArrayList<AdPackages>
):Pagination()
