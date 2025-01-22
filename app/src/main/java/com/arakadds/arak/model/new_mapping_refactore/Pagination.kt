package com.arakadds.arak.model.new_mapping_refactore

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Pagination() : Serializable, Parcelable {

    @SerializedName("total")
    var total: Int = 0

    @SerializedName("page")
    var page: Int = 0

    @SerializedName("lastPage")
    var lastPage: Int = 0

    constructor(parcel: Parcel) : this() {
        total = parcel.readInt()
        page = parcel.readInt()
        lastPage = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(total)
        parcel.writeInt(page)
        parcel.writeInt(lastPage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pagination> {
        override fun createFromParcel(parcel: Parcel): Pagination {
            return Pagination(parcel)
        }

        override fun newArray(size: Int): Array<Pagination?> {
            return arrayOfNulls(size)
        }
    }


}
