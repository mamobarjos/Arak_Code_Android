package com.arakadds.arak.model.new_mapping_refactore.request.ad

import com.google.gson.annotations.SerializedName

class AdRequest {

    @SerializedName("title")
    var title: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("phone_no")
    var phoneNo: String? = null

    @SerializedName("alternative_phone_no")
    var alternativePhoneNo: String? = null

    @SerializedName("website_url")
    var websiteUrl: String? = null

  /*  @SerializedName("thumb_url")
    var thumbUrl: String? = null*/

    @SerializedName("lon")
    var lon: String? = null

    @SerializedName("lat")
    var lat: String? = null

    @SerializedName("duration")
    var duration: Int = 0

    @SerializedName("ad_category_id")
    var adCategoryId: Int = 0

    @SerializedName("ad_package_id")
    var adPackageId: Int = 0

    @SerializedName("payment_type")
    var paymentType: String? = null

    @SerializedName("ad_files")
    var adFiles: ArrayList<AdFile>? = null
}
