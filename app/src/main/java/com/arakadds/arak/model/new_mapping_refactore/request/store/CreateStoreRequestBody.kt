package com.arakadds.arak.model.new_mapping_refactore.request.store

import com.google.gson.annotations.SerializedName

class CreateStoreRequestBody {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("phone_no")
    var phoneNo: String? = null

    @SerializedName("img_url")
    var imgUrl: String? = null

    @SerializedName("cover_img_url")
    var coverImgUrl: String? = null

    @SerializedName("store_category_id")
    var storeCategoryId: Int = 0

    @SerializedName("city_id")
    var cityId: Int = 0

    @SerializedName("has_visa")
    var hasVisa: Boolean = false

    @SerializedName("has_mastercard")
    var hasMastercard: Boolean = false

    @SerializedName("has_cash")
    var hasCash: Boolean = false

    @SerializedName("has_paypal")
    var hasPaypal: Boolean = false

    @SerializedName("lon")
    var lon: String? = null

    @SerializedName("lat")
    var lat: String? = null

    @SerializedName("location_name")
    var locationName: String? = null

    @SerializedName("website")
    var website: String? = null

    @SerializedName("facebook")
    var facebook: String? = null

    @SerializedName("x")
    var x: String? = null

    @SerializedName("instagram")
    var instagram: String? = null

    @SerializedName("linkedin")
    var linkedin: String? = null

    @SerializedName("snapchat")
    var snapchat: String? = null

    @SerializedName("tiktok")
    var tiktok: String? = null

    @SerializedName("youtube")
    var youtube: String? = null

}