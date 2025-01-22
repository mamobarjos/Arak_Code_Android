package com.arakadds.arak.model.new_mapping_refactore.response.banners.edit_user_info

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.UserObject
import com.google.gson.annotations.SerializedName

data class EditUserInformation(

    @SerializedName("data")
    var userObject: UserObject

):BaseResponse()
