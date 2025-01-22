package com.arakadds.arak.model.new_mapping_refactore.response.banners.auth

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserObject(
    @SerializedName("id") val id: Int,
    @SerializedName("fullname") val fullname: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("phone_no") val phoneNo: String?,
    @SerializedName("birthdate") val birthdate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("city_id") val cityId: Int,
    @SerializedName("country_name") val countryName: String?,
    @SerializedName("city_name") val cityName: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("balance") val balance: String?,
    @SerializedName("img_avatar") val imgAvatar: String?,
    @SerializedName("notifications_enabled") val notificationsEnabled: Boolean,
    @SerializedName("fcm_token") val fcmToken: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("has_store") val hasStore: Boolean,
    @SerializedName("has_wallet") val hasWallet: Boolean,
    @SerializedName("ads_imgs_views") val adsImgsViews: Int,
    @SerializedName("ads_videos_views") val adsVideosViews: Int,
    @SerializedName("ads_website_views") val adsWebsiteViews: Int,
    //@SerializedName("preferences") val userPreferences: UserPreferences?,
    @SerializedName("oauthId") val oauthId: String?,
    @SerializedName("sl_provider") val slProvider: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("deleted_at") val deletedAt: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        fullname = parcel.readString(),
        password = parcel.readString(),
        phoneNo = parcel.readString(),
        birthdate = parcel.readString(),
        gender = parcel.readString(),
        countryId = parcel.readInt(),
        cityId = parcel.readInt(),
        countryName = parcel.readString(),
        cityName = parcel.readString(),
        role = parcel.readString(),
        balance = parcel.readString(),
        imgAvatar = parcel.readString(),
        notificationsEnabled = parcel.readByte() != 0.toByte(),
        fcmToken = parcel.readString(),
        isActive = parcel.readByte() != 0.toByte(),
        hasStore = parcel.readByte() != 0.toByte(),
        hasWallet = parcel.readByte() != 0.toByte(),
        adsImgsViews = parcel.readInt(),
        adsVideosViews = parcel.readInt(),
        adsWebsiteViews = parcel.readInt(),
        //userPreferences = parcel.readParcelable(UserPreferences::class.java.classLoader)!!,
        oauthId = parcel.readString(),
        slProvider = parcel.readString(),
        createdAt = parcel.readString(),
        updatedAt = parcel.readString(),
        deletedAt = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(fullname)
        parcel.writeString(password)
        parcel.writeString(phoneNo)
        parcel.writeString(birthdate)
        parcel.writeString(gender)
        parcel.writeInt(countryId)
        parcel.writeInt(cityId)
        parcel.writeString(countryName)
        parcel.writeString(cityName)
        parcel.writeString(role)
        parcel.writeString(balance)
        parcel.writeString(imgAvatar)
        parcel.writeByte(if (notificationsEnabled) 1 else 0)
        parcel.writeString(fcmToken)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeByte(if (hasStore) 1 else 0)
        parcel.writeByte(if (hasWallet) 1 else 0)
        parcel.writeInt(adsImgsViews)
        parcel.writeInt(adsVideosViews)
        parcel.writeInt(adsWebsiteViews)
        //parcel.writeParcelable(userPreferences, flags)  // Corrected this line
        parcel.writeString(oauthId)
        parcel.writeString(slProvider)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UserObject> {
        override fun createFromParcel(parcel: Parcel): UserObject {
            return UserObject(parcel)
        }

        override fun newArray(size: Int): Array<UserObject?> {
            return arrayOfNulls(size)
        }
    }
}
