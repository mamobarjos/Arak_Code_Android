package com.arakadds.arak.model.new_mapping_refactore.response.banners.withdraw

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.google.gson.annotations.SerializedName

data class WithdrawRequestsData(

    @SerializedName("withdrawRequests") val withdrawRequests: ArrayList<WithdrawRequest>,

    ):Pagination()
