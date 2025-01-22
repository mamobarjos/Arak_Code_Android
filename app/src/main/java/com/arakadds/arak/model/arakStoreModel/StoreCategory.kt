package com.arakadds.arak.model.arakStoreModel

import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName

data class StoreCategory(


    @SerializedName("data") var data: ArrayList<StoreCategoryData> = arrayListOf(),
    @SerializedName("extra") var extra: ArrayList<String> = arrayListOf()

) : BaseResponse()


data class Image(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("date_created") var dateCreated: String? = null,
    @SerializedName("date_created_gmt") var dateCreatedGmt: String? = null,
    @SerializedName("date_modified") var dateModified: String? = null,
    @SerializedName("date_modified_gmt") var dateModifiedGmt: String? = null,
    @SerializedName("src") var src: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("alt") var alt: String? = null

)

data class Self(

    @SerializedName("href") var href: String? = null

)


data class Collection(

    @SerializedName("href") var href: String? = null

)

data class Links(

    @SerializedName("self") var self: ArrayList<Self> = arrayListOf(),
    @SerializedName("collection") var collection: ArrayList<Collection> = arrayListOf()

) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("self"),
        TODO("collection")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Links> {
        override fun createFromParcel(parcel: Parcel): Links {
            return Links(parcel)
        }

        override fun newArray(size: Int): Array<Links?> {
            return arrayOfNulls(size)
        }
    }
}

data class StoreCategoryData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    var nameAr: String? = null,
    var image2: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("parent") var parent: Int? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("display") var display: String? = null,
    @SerializedName("image") var image: Image? = Image(),
    @SerializedName("menu_order") var menuOrder: Int? = null,
    @SerializedName("count") var count: Int? = null,
    @SerializedName("_links") var Links: Links? = Links()

)