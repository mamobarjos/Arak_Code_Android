package com.arakadds.arak.model.new_mapping_refactore.store

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StoreProduct(
    @SerializedName("store_product_files") val storeProductFiles: List<StoreProductFile>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(StoreProductFile) ?: emptyList())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(storeProductFiles)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreProduct> {
        override fun createFromParcel(parcel: Parcel): StoreProduct {
            return StoreProduct(parcel)
        }

        override fun newArray(size: Int): Array<StoreProduct?> {
            return arrayOfNulls(size)
        }
    }
}
