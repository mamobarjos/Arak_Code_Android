package com.arakadds.arak.model.new_mapping_refactore.home

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") var id: Int= 0,
    @SerializedName("fullname") var fullName: String?= null,
    @SerializedName("phone_no") var phoneNo: String?= null,
    @SerializedName("country_id") val countryId: Int?= null,
    @SerializedName("city_id") val cityId: Int?= 0,
    @SerializedName("img_avatar") var imgAvatar: String?= null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(fullName)
        parcel.writeString(phoneNo)
        parcel.writeValue(countryId)
        parcel.writeValue(cityId)
        parcel.writeString(imgAvatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
