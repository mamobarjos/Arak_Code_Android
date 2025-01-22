package com.arakadds.arak.model.new_mapping_refactore.store

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.google.gson.annotations.SerializedName

data class SingleStoreModel(

    @SerializedName("data")
    var storeObject: StoreObject
) : BaseResponse()
