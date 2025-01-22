package com.arakadds.arak.model.new_mapping_refactore.response.banners.auth

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserPreferences(
    @SerializedName("categories") val categories: List<Int>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        ArrayList<Int>().apply {
            parcel.readList(this, Int::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(categories)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserPreferences> {
        override fun createFromParcel(parcel: Parcel): UserPreferences {
            return UserPreferences(parcel)
        }

        override fun newArray(size: Int): Array<UserPreferences?> {
            return arrayOfNulls(size)
        }
    }
}
