package com.arakadds.arak.model.new_mapping_refactore.request.auth
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.Date

class RegistrationBodyRequest() : Parcelable {

    @SerializedName("fullname")
    var fullname: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("phone_no")
    var phoneNo: String? = null

    @SerializedName("birthdate")
    var birthdate: Date? = null

    @SerializedName("country_id")
    var countryId: Int = 0

    @SerializedName("city_id")
    var cityId: Int = 0

    @SerializedName("otp_code")
    var otpCode: String? = null

    @SerializedName("email")
    var userEmail: String? = null

    constructor(parcel: Parcel) : this() {
        fullname = parcel.readString()
        gender = parcel.readString()
        password = parcel.readString()
        phoneNo = parcel.readString()
        birthdate = parcel.readLong().let { if (it != -1L) Date(it) else null }
        countryId = parcel.readInt()
        cityId = parcel.readInt()
        otpCode = parcel.readString()
        userEmail = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullname)
        parcel.writeString(gender)
        parcel.writeString(password)
        parcel.writeString(phoneNo)
        parcel.writeLong(birthdate?.time ?: -1L)
        parcel.writeInt(countryId)
        parcel.writeInt(cityId)
        parcel.writeString(otpCode)
        parcel.writeString(userEmail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegistrationBodyRequest> {
        override fun createFromParcel(parcel: Parcel): RegistrationBodyRequest {
            return RegistrationBodyRequest(parcel)
        }

        override fun newArray(size: Int): Array<RegistrationBodyRequest?> {
            return arrayOfNulls(size)
        }
    }
}
