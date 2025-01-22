package com.arakadds.arak.model.new_mapping_refactore.request.auth

import android.text.Editable
import com.google.gson.annotations.SerializedName

data class OTPRequest(
    @SerializedName("phone_no")
    var phoneNumber:String,

    @SerializedName("email")
    var email: String?
)
