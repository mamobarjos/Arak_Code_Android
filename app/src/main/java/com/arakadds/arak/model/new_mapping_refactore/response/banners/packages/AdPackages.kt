package com.arakadds.arak.model.new_mapping_refactore.response.banners.packages

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AdPackages(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name_ar") val nameAr: String? = null,
    @SerializedName("name_en") val nameEn: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("reach") val reach: Int = 0,
    @SerializedName("price") val price: String? = null,
    @SerializedName("seconds") val seconds: Int = 0,
    @SerializedName("no_of_imgs") val numberOfImages: Int = 0,
    @SerializedName("ad_category_id") val adCategoryId: Int = 0,
    @SerializedName("country_id") val countryId: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("deleted_at") val deletedAt: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        nameAr = parcel.readString(),
        nameEn = parcel.readString(),
        type = parcel.readString(),
        reach = parcel.readInt(),
        price = parcel.readString(),
        seconds = parcel.readInt(),
        numberOfImages = parcel.readInt(),
        adCategoryId = parcel.readInt(),
        countryId = parcel.readInt(),
        createdAt = parcel.readString(),
        updatedAt = parcel.readString(),
        deletedAt = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nameAr)
        parcel.writeString(nameEn)
        parcel.writeString(type)
        parcel.writeInt(reach)
        parcel.writeString(price)
        parcel.writeInt(seconds)
        parcel.writeInt(numberOfImages)
        parcel.writeInt(adCategoryId)
        parcel.writeInt(countryId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdPackages> {
        override fun createFromParcel(parcel: Parcel): AdPackages {
            return AdPackages(parcel)
        }

        override fun newArray(size: Int): Array<AdPackages?> {
            return arrayOfNulls(size)
        }
    }
}
