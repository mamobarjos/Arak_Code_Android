package com.arakadds.arak.model.new_mapping_refactore

import com.google.gson.annotations.SerializedName
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

open class BaseResponse() : Serializable, Parcelable {
    @SerializedName("status_code")
    var statusCode: Int? = null

    @SerializedName("message")
    var message: String? = null
    //var isSuccess: Boolean? = null

    constructor(parcel: Parcel) : this() {
        statusCode = parcel.readValue(Int::class.java.classLoader) as? Int
        message = parcel.readString()
        //isSuccess = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(statusCode)
        parcel.writeString(message)
        //parcel.writeValue(isSuccess)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseResponse> {
        override fun createFromParcel(parcel: Parcel): BaseResponse {
            return BaseResponse(parcel)
        }

        override fun newArray(size: Int): Array<BaseResponse?> {
            return arrayOfNulls(size)
        }
    }
}