package com.arakadds.arak.model.new_mapping_refactore.request.create_store_product

import com.arakadds.arak.model.new_mapping_refactore.request.StoreProductFileRequest
import com.google.gson.annotations.SerializedName

class CreateProductRequestBody{

    @SerializedName("name")
    var name: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("price")
    var price: Double? = null

    @SerializedName("sale_price")
    var salePrice: Double? = null

    @SerializedName("store_id")
    var storeId: Int? = null

    @SerializedName("store_product_files")
    var storeProductFiles: ArrayList<StoreProductFileRequest>? = null
}
