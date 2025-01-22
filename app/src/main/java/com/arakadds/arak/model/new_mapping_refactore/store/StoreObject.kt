package com.arakadds.arak.model.new_mapping_refactore.store

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StoreObject(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("phone_no") val phoneNo: String?,
    @SerializedName("img_url") val imgUrl: String?,
    @SerializedName("cover_img_url") val coverImgUrl: String?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("rating") val rating: Float?,
    @SerializedName("website") val website: String?,
    @SerializedName("facebook") val facebook: String?,
    @SerializedName("x") val x: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("linkedin") val linkedin: String?,
    @SerializedName("snapchat") val snapchat: String?,
    @SerializedName("tiktok") val tiktok: String?,
    @SerializedName("youtube") val youtube: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("is_featured") val isFeatured: Boolean,
    @SerializedName("has_visa") val hasVisa: Boolean,
    @SerializedName("has_mastercard") val hasMastercard: Boolean,
    @SerializedName("has_paypal") val hasPaypal: Boolean,
    @SerializedName("has_cash") val hasCash: Boolean,
    @SerializedName("store_category_id") val storeCategoryId: Int,
    @SerializedName("country_id") val countryId: Int?,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("store_category") val storeCategory: StoreCategory?,
    @SerializedName("store_products") val storeProducts: List<StoreProduct>,
    @SerializedName("store_reviews") val storeReviews: List<StoreProductReview>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Float::class.java.classLoader) as? Float, // Properly handle nullable Float
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(StoreCategory::class.java.classLoader),
        parcel.createTypedArrayList(StoreProduct.CREATOR) ?: emptyList(),
        parcel.createTypedArrayList(StoreProductReview.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(phoneNo)
        parcel.writeString(imgUrl)
        parcel.writeString(coverImgUrl)
        parcel.writeValue(lon)
        parcel.writeValue(lat)
        parcel.writeValue(rating) // Correctly parcel the nullable Float
        parcel.writeString(website)
        parcel.writeString(facebook)
        parcel.writeString(x)
        parcel.writeString(instagram)
        parcel.writeString(linkedin)
        parcel.writeString(snapchat)
        parcel.writeString(tiktok)
        parcel.writeString(youtube)
        parcel.writeString(status)
        parcel.writeByte(if (isFeatured) 1 else 0)
        parcel.writeByte(if (hasVisa) 1 else 0)
        parcel.writeByte(if (hasMastercard) 1 else 0)
        parcel.writeByte(if (hasPaypal) 1 else 0)
        parcel.writeByte(if (hasCash) 1 else 0)
        parcel.writeInt(storeCategoryId)
        parcel.writeValue(countryId)
        parcel.writeInt(userId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
        parcel.writeParcelable(storeCategory, flags)
        parcel.writeTypedList(storeProducts)
        parcel.writeTypedList(storeReviews)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreObject> {
        override fun createFromParcel(parcel: Parcel): StoreObject {
            return StoreObject(parcel)
        }

        override fun newArray(size: Int): Array<StoreObject?> {
            return arrayOfNulls(size)
        }
    }
}
