package com.arakadds.arak.model.new_mapping_refactore.store.categories

import com.google.gson.annotations.SerializedName

data class StoreCategoriesData(
    @SerializedName("storeCategories") val storeCategories: ArrayList<StoreCategory>,
)