package com.arakadds.arak.model.new_mapping_refactore.store

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StoreCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String?,
    @SerializedName("name_en") val nameEn: String?,
    @SerializedName("name") val name: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nameAr)
        parcel.writeString(nameEn)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreCategory> {
        override fun createFromParcel(parcel: Parcel): StoreCategory {
            return StoreCategory(parcel)
        }

        override fun newArray(size: Int): Array<StoreCategory?> {
            return arrayOfNulls(size)
        }
    }
}
