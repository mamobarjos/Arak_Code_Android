package com.arakadds.arak.model.new_mapping_refactore.response.banners.ranking

import com.arakadds.arak.model.new_mapping_refactore.Pagination
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.UserObject
import com.google.gson.annotations.SerializedName

data class UsersRankingData(
    @SerializedName("users") val userObjectList: List<UserObject>,
) : Pagination()
