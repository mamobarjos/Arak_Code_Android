package com.arakadds.arak.model.new_mapping_refactore.response.banners.elected

import com.google.gson.annotations.SerializedName

data class ElectedPeopleData(
    @SerializedName("electedPeople") val electedPeople: List<ElectedPerson>
)
