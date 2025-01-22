package com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews

import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.model.new_mapping_refactore.home.User
import com.google.gson.annotations.SerializedName
data class AdReviews(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("ad_id") val adId: Int,// retrived with ad-reviews
    @SerializedName("user_id") val userId: Int,// retreved with ad-reviews and feedback
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("user") val user: User?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(content)
        parcel.writeString(rating)
        parcel.writeInt(adId)
        parcel.writeInt(userId)
        parcel.writeString(createdAt)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdReviews> {
        override fun createFromParcel(parcel: Parcel): AdReviews {
            return AdReviews(parcel)
        }

        override fun newArray(size: Int): Array<AdReviews?> {
            return arrayOfNulls(size)
        }
    }
}