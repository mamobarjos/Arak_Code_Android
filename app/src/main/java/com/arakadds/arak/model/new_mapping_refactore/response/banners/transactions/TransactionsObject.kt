package com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions

import com.google.gson.annotations.SerializedName

data class TransactionsObject(
    @SerializedName("id") val id: Int,
    @SerializedName("transaction_id") val transactionId: String,
    @SerializedName("description") val description: String,
    @SerializedName("payment_brand") val paymentBrand: String?,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?
)
