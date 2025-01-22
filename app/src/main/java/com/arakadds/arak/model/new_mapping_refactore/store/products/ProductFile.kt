package com.arakadds.arak.model.new_mapping_refactore.store.products

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ProductFile(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String?,
    @SerializedName("store_product_file") val storeProductFile: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(url)
        parcel.writeInt(storeProductFile)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductFile> {
        override fun createFromParcel(parcel: Parcel): ProductFile {
            return ProductFile(parcel)
        }

        override fun newArray(size: Int): Array<ProductFile?> {
            return arrayOfNulls(size)
        }
    }
}
