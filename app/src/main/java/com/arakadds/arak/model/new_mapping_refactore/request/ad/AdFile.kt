package com.arakadds.arak.model.new_mapping_refactore.request.ad

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class AdFile(
    @SerializedName("url")
    var url: String? = null
) : Parcelable {

    // Constructor to create the object from a Parcel
    private constructor(parcel: Parcel) : this(
        url = parcel.readString()
    )

    // Method to write the object's data to the passed-in Parcel
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
    }

    // Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
    // For most objects, you can simply return 0.
    override fun describeContents(): Int {
        return 0
    }

    // Static field used to regenerate your object, individually or as arrays
    companion object CREATOR : Parcelable.Creator<AdFile> {
        override fun createFromParcel(parcel: Parcel): AdFile {
            return AdFile(parcel)
        }

        override fun newArray(size: Int): Array<AdFile?> {
            return arrayOfNulls(size)
        }
    }
}