package com.arakadds.arak.model.new_mapping_refactore.response.banners.elected

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class SingleElectedPersonModel(

    @SerializedName("data") val electedPerson: ElectedPerson

) : BaseResponse()
