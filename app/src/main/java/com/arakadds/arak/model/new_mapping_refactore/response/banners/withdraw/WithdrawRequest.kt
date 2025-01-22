package com.arakadds.arak.model.new_mapping_refactore.response.banners.withdraw

import com.arakadds.arak.model.new_mapping_refactore.home.User
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.UserObject
import com.google.gson.annotations.SerializedName

data class WithdrawRequest(

    @SerializedName("id") val id: Int,
    @SerializedName("amount") val amount: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("wallet_type") val walletType: String,
    @SerializedName("status") val status: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("user") val user: UserObject
)
