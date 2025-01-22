package com.arakadds.arak.model.new_mapping_refactore.request.user_interests

import com.google.gson.annotations.SerializedName

data class UserInterestsRequest(
    @SerializedName("category_ids")
    var categoryIdsArrayList: ArrayList<Int>
)
