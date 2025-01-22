package com.arakadds.arak.model.new_mapping_refactore.store.products

import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: String?,
    @SerializedName("sale_price") val salePrice: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("store_product_files") val storeProductFiles: List<ProductFile>, // Use List instead of ArrayList
    @SerializedName("store") val store: StoreObject?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(ProductFile.CREATOR) ?: emptyList(), // Correct way to handle list
        parcel.readParcelable(StoreObject::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeString(salePrice)
        parcel.writeString(rating)
        parcel.writeInt(storeId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
        parcel.writeTypedList(storeProductFiles) // Correct way to handle list
        parcel.writeParcelable(store, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}