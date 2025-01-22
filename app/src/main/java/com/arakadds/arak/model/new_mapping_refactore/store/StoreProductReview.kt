package com.arakadds.arak.model.new_mapping_refactore.store

import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.model.new_mapping_refactore.home.User
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ReviewUser
import com.google.gson.annotations.SerializedName

data class StoreProductReview(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("store_id") val storeId: Int?=null,// for store reviews
    @SerializedName("store_product_id") val storeProductId: Int?=null,// for store product reviews
    @SerializedName("user_id") val userId: Int?=null,// retreved with ad-reviews and feedback
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("user") var user: User?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(content)
        parcel.writeString(rating)
        parcel.writeValue(storeId)
        parcel.writeValue(storeProductId)
        parcel.writeValue(userId)
        parcel.writeString(createdAt)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreProductReview> {
        override fun createFromParcel(parcel: Parcel): StoreProductReview {
            return StoreProductReview(parcel)
        }

        override fun newArray(size: Int): Array<StoreProductReview?> {
            return arrayOfNulls(size)
        }
    }
}