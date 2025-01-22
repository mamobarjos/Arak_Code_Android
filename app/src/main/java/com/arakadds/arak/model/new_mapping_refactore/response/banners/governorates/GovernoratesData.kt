package com.arakadds.arak.model.new_mapping_refactore.response.banners.governorates

import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.Governorate
import com.google.gson.annotations.SerializedName

data class GovernoratesData(
    @SerializedName("governorates") val governorates: List<Governorate>
)
