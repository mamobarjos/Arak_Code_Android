package com.arakadds.arak.model.new_mapping_refactore.store

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class StoresData(

    @SerializedName("stores") val stores: ArrayList<StoreObject>,
):Pagination()
