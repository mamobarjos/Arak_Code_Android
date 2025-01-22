package com.arakadds.arak.model.new_mapping_refactore.home

import android.os.Build
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.AdReviews
import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdsData(

    @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("duration") var duration: Int,
    @SerializedName("status") var status: String?,
    @SerializedName("is_featured") var isFeatured: Boolean,
    @SerializedName("is_payment_completed") var isPaymentCompleted: Boolean,
    @SerializedName("phone_no") var phoneNumber: String?,
    @SerializedName("alternative_phone_no") var alternativePhoneNumber: String?,
    @SerializedName("website_url") var websiteURL: String?,
    @SerializedName("checkout_url") var checkoutUrl : String?,
    @SerializedName("lon") var lon: String?,
    @SerializedName("lat") var lat: String?,
    @SerializedName("views") var views: Int,
    @SerializedName("reach") var reach: Int,
    @SerializedName("ad_category_id") var adCategoryId: Int,
    @SerializedName("ad_package_id") var adPackageId: Int,
    @SerializedName("country_id") var countryId: Int,
    @SerializedName("user_id") var userId: Int,
    @SerializedName("store_id") var storeId: Int?,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("updated_at") var updatedAt: String?,
    @SerializedName("is_favorite") var isFavorite: Boolean,
    @SerializedName("is_reviewed") var isReviewed: Boolean,
    @SerializedName("rating") var rating: Float,
    @SerializedName("ad_files") val adFiles: ArrayList<StoreProductFile> = ArrayList(),
    @SerializedName("ad_category") val adCategory: AdCategory?,
    @SerializedName("ad_package") val adPackage: AdPackages?,
    @SerializedName("ad_reviews") val adRreviewsArrayList: ArrayList<AdReviews> = ArrayList(),
    @SerializedName("country") val country: Country?,
    @SerializedName("user") val user: User?

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        title = parcel.readString(),
        description = parcel.readString(),
        duration = parcel.readInt(),
        status = parcel.readString(),
        isFeatured = parcel.readByte() != 0.toByte(),
        isPaymentCompleted = parcel.readByte() != 0.toByte(),
        phoneNumber = parcel.readString(),
        alternativePhoneNumber = parcel.readString(),
        websiteURL = parcel.readString(),
        checkoutUrl = parcel.readString(),
        lon = parcel.readString(),
        lat = parcel.readString(),
        views = parcel.readInt(),
        reach = parcel.readInt(),
        adCategoryId = parcel.readInt(),
        adPackageId = parcel.readInt(),
        countryId = parcel.readInt(),
        userId = parcel.readInt(),
        storeId = parcel.readInt(),
        createdAt = parcel.readString(),
        updatedAt = parcel.readString(),
        isFavorite = parcel.readByte() != 0.toByte(),
        isReviewed = parcel.readByte() != 0.toByte(),
        rating = parcel.readFloat(),
        adFiles = parcel.createTypedArrayList(StoreProductFile.CREATOR) ?: ArrayList(),
        adCategory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(AdCategory::class.java.classLoader, AdCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(AdCategory::class.java.classLoader)
        },
        adPackage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(AdPackages::class.java.classLoader, AdPackages::class.java)
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(AdPackages::class.java.classLoader)
        },
        country = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(Country::class.java.classLoader, Country::class.java)
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(Country::class.java.classLoader)
        },
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(User::class.java.classLoader, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(User::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(duration)
        parcel.writeString(status)
        parcel.writeByte(if (isFeatured) 1 else 0)
        parcel.writeByte(if (isPaymentCompleted) 1 else 0)
        parcel.writeString(phoneNumber)
        parcel.writeString(alternativePhoneNumber)
        parcel.writeString(websiteURL)
        parcel.writeString(checkoutUrl)
        parcel.writeString(lon)
        parcel.writeString(lat)
        parcel.writeInt(views)
        parcel.writeInt(reach)
        parcel.writeInt(adCategoryId)
        parcel.writeInt(adPackageId)
        parcel.writeInt(countryId)
        parcel.writeInt(userId)
        storeId?.let { parcel.writeInt(it) }
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isReviewed) 1 else 0)
        parcel.writeFloat(rating)
        parcel.writeTypedList(adFiles)
        parcel.writeParcelable(adCategory, flags)
        parcel.writeParcelable(adPackage, flags)
        parcel.writeParcelable(country, flags)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdsData> {
        override fun createFromParcel(parcel: Parcel): AdsData {
            return AdsData(parcel)
        }

        override fun newArray(size: Int): Array<AdsData?> {
            return arrayOfNulls(size)
        }
    }
}
