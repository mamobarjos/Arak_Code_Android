package com.arakadds.arak.model.new_mapping_refactore.store

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
data class SingleProductDataModel(
    @SerializedName("id") val productId: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("sale_price") val salePrice: String? = null,
    @SerializedName("rating") val rating: String? = "0.0",
    @SerializedName("store_id") val storeId: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("store_product_files") val adFilesArrayList: ArrayList<StoreProductFile>? = null,
    @SerializedName("store_product_reviews") val productReviews: ArrayList<StoreProductReview>? = null,
    @SerializedName("store") val storeObject: ProductStoreInformation? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        productId = parcel.readInt(),
        name = parcel.readString(),
        description = parcel.readString(),
        price = parcel.readString(),
        salePrice = parcel.readString(),
        rating = parcel.readString(),
        storeId = parcel.readInt(),
        createdAt = parcel.readString(),
        updatedAt = parcel.readString(),
        adFilesArrayList = parcel.createTypedArrayList(StoreProductFile.CREATOR),
        productReviews = parcel.createTypedArrayList(StoreProductReview.CREATOR),
        storeObject = parcel.readParcelable(ProductStoreInformation::class.java.classLoader)
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(productId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeString(salePrice)
        parcel.writeString(rating)
        parcel.writeInt(storeId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeTypedList(adFilesArrayList)
        parcel.writeTypedList(productReviews)
        parcel.writeParcelable(storeObject, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SingleProductDataModel> {
        override fun createFromParcel(parcel: Parcel): SingleProductDataModel {
            return SingleProductDataModel(parcel)
        }

        override fun newArray(size: Int): Array<SingleProductDataModel?> {
            return arrayOfNulls(size)
        }
    }
}