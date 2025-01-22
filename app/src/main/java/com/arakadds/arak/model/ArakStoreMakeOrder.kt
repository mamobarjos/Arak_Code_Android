package com.arakadds.arak.model

import com.google.gson.annotations.SerializedName

data class ArakStoreMakeOrder (

    @SerializedName("status_code" ) var statusCode : Int?              = null,
    @SerializedName("message"     ) var message    : String?           = null,
    @SerializedName("data"        ) var data       : ArakStoreMakeOrderData?             = ArakStoreMakeOrderData(),
    @SerializedName("extra"       ) var extra      : ArrayList<String> = arrayListOf()

)

data class ArakStoreMakeOrderData (

    @SerializedName("success"      ) var success     : Boolean? = null,
    @SerializedName("checkout_id"  ) var checkoutId  : String?  = null,
    @SerializedName("checkout_url" ) var checkoutUrl : String?  = null

)