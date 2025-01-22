package com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class TransactionData(
    @SerializedName("data") val transactions: ArrayList<TransactionsObject>,
):Pagination()
