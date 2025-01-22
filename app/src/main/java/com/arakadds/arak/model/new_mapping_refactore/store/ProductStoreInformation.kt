package com.arakadds.arak.model.new_mapping_refactore.store

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ProductStoreInformation(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("phone_no") val phoneNo: String?,
    @SerializedName("img_url") val imgUrl: String?,
    @SerializedName("store_category") val storeCategory: StoreCategory?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(StoreCategory::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(phoneNo)
        parcel.writeString(imgUrl)
        parcel.writeParcelable(storeCategory, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductStoreInformation> {
        override fun createFromParcel(parcel: Parcel): ProductStoreInformation {
            return ProductStoreInformation(parcel)
        }

        override fun newArray(size: Int): Array<ProductStoreInformation?> {
            return arrayOfNulls(size)
        }
    }
}
