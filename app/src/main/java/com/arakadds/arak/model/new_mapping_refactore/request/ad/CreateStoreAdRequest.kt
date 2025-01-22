package com.arakadds.arak.model.new_mapping_refactore.request.ad

import com.google.gson.annotations.SerializedName

class CreateStoreAdRequest {

    @SerializedName("payment_type")
    var paymentType: String? = null

    @SerializedName("ad_category_id")
    var adCategoryId: Int? = null

    @SerializedName("ad_package_id")
    var adPackageId: Int? = null

    @SerializedName("store_id")
    var storeId: Int? = null
}




