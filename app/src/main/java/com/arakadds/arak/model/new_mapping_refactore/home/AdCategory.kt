package com.arakadds.arak.model.new_mapping_refactore.home

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AdCategory(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name_en") val nameEn: String? = null,
    @SerializedName("name_ar") val nameAr: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        nameEn = parcel.readString(),
        nameAr = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nameEn)
        parcel.writeString(nameAr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdCategory> {
        override fun createFromParcel(parcel: Parcel): AdCategory {
            return AdCategory(parcel)
        }

        override fun newArray(size: Int): Array<AdCategory?> {
            return arrayOfNulls(size)
        }
    }
}
