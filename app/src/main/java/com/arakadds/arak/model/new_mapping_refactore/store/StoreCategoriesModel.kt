package com.arakadds.arak.model.new_mapping_refactore.store

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.categories.StoreCategoriesData
import com.google.gson.annotations.SerializedName

data class StoreCategoriesModel(
    @SerializedName("data") val data: StoreCategoriesData,
) : BaseResponse()
