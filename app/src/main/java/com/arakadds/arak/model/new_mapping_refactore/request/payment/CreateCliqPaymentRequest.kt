package com.arakadds.arak.model.new_mapping_refactore.request.payment

import com.google.gson.annotations.SerializedName

data class CreateCliqPaymentRequest(
    @SerializedName("amount") var amount: String,
    @SerializedName("img_url") var img_url: String,
)
