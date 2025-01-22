package com.arakadds.arak.model.new_mapping_refactore.home

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("id") val id: Int= 0,
    @SerializedName("name_en") val nameEn: String?= null,
    @SerializedName("name_ar") val nameAr: String?= null,
    @SerializedName("country_code") val countryCode: String?= null,
    @SerializedName("currency_id") val currencyId: Int= 0,
    @SerializedName("created_at") val createdAt: String?= null,
    @SerializedName("updated_at") val updatedAt: String?= null,
    @SerializedName("deleted_at") val deletedAt: String?= null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nameEn)
        parcel.writeString(nameAr)
        parcel.writeString(countryCode)
        parcel.writeInt(currencyId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Country> {
        override fun createFromParcel(parcel: Parcel): Country {
            return Country(parcel)
        }

        override fun newArray(size: Int): Array<Country?> {
            return arrayOfNulls(size)
        }
    }
}