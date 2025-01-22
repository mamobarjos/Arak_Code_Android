package com.arakadds.arak.model

import com.google.gson.annotations.SerializedName

data class ProductVariant (

    @SerializedName("status_code" ) var statusCode : Int?              = null,
    @SerializedName("message"     ) var message    : String?           = null,
    @SerializedName("data"        ) var data       : ArrayList<Data>   = arrayListOf(),
    @SerializedName("extra"       ) var extra      : ArrayList<String> = arrayListOf()

)


data class Dimensions (

    @SerializedName("length" ) var length : String? = null,
    @SerializedName("width"  ) var width  : String? = null,
    @SerializedName("height" ) var height : String? = null

)


data class Image (

    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("date_created"      ) var dateCreated     : String? = null,
    @SerializedName("date_created_gmt"  ) var dateCreatedGmt  : String? = null,
    @SerializedName("date_modified"     ) var dateModified    : String? = null,
    @SerializedName("date_modified_gmt" ) var dateModifiedGmt : String? = null,
    @SerializedName("src"               ) var src             : String? = null,
    @SerializedName("name"              ) var name            : String? = null,
    @SerializedName("alt"               ) var alt             : String? = null

)



data class Attributes (

    @SerializedName("id"     ) var id     : Int?    = null,
    @SerializedName("name"   ) var name   : String? = null,
    @SerializedName("slug"   ) var slug   : String? = null,
    @SerializedName("option" ) var option : String? = null

)

data class MetaData (

    @SerializedName("id"    ) var id    : Int?    = null,
    @SerializedName("key"   ) var key   : String? = null,
    @SerializedName("value" ) var value : String? = null

)


data class Links (

    @SerializedName("self"       ) var self       : ArrayList<Self>       = arrayListOf(),
    @SerializedName("collection" ) var collection : ArrayList<Collection> = arrayListOf(),
    @SerializedName("up"         ) var up         : ArrayList<Up>         = arrayListOf()

)

data class Up (

    @SerializedName("href" ) var href : String? = null

)

data class Self (

    @SerializedName("href" ) var href : String? = null

)


data class Collection (

    @SerializedName("href" ) var href : String? = null

)

data class Data (

    @SerializedName("id"                    ) var id                : Int?                  = null,
    @SerializedName("type"                  ) var type              : String?               = null,
    @SerializedName("date_created"          ) var dateCreated       : String?               = null,
    @SerializedName("date_created_gmt"      ) var dateCreatedGmt    : String?               = null,
    @SerializedName("date_modified"         ) var dateModified      : String?               = null,
    @SerializedName("date_modified_gmt"     ) var dateModifiedGmt   : String?               = null,
    @SerializedName("description"           ) var description       : String?               = null,
    @SerializedName("permalink"             ) var permalink         : String?               = null,
    @SerializedName("sku"                   ) var sku               : String?               = null,
    @SerializedName("global_unique_id"      ) var globalUniqueId    : String?               = null,
    @SerializedName("price"                 ) var price             : String?               = null,
    @SerializedName("regular_price"         ) var regularPrice      : String?               = null,
    @SerializedName("sale_price"            ) var salePrice         : String?               = null,
    @SerializedName("date_on_sale_from"     ) var dateOnSaleFrom    : String?               = null,
    @SerializedName("date_on_sale_from_gmt" ) var dateOnSaleFromGmt : String?               = null,
    @SerializedName("date_on_sale_to"       ) var dateOnSaleTo      : String?               = null,
    @SerializedName("date_on_sale_to_gmt"   ) var dateOnSaleToGmt   : String?               = null,
    @SerializedName("on_sale"               ) var onSale            : Boolean?              = null,
    @SerializedName("status"                ) var status            : String?               = null,
    @SerializedName("purchasable"           ) var purchasable       : Boolean?              = null,
    @SerializedName("virtual"               ) var virtual           : Boolean?              = null,
    @SerializedName("downloadable"          ) var downloadable      : Boolean?              = null,
    @SerializedName("downloads"             ) var downloads         : ArrayList<String>     = arrayListOf(),
    @SerializedName("download_limit"        ) var downloadLimit     : Int?                  = null,
    @SerializedName("download_expiry"       ) var downloadExpiry    : Int?                  = null,
    @SerializedName("tax_status"            ) var taxStatus         : String?               = null,
    @SerializedName("tax_class"             ) var taxClass          : String?               = null,
    @SerializedName("manage_stock"          ) var manageStock       : Boolean?              = null,
    @SerializedName("stock_quantity"        ) var stockQuantity     : Int?                  = null,
    @SerializedName("stock_status"          ) var stockStatus       : String?               = null,
    @SerializedName("backorders"            ) var backorders        : String?               = null,
    @SerializedName("backorders_allowed"    ) var backordersAllowed : Boolean?              = null,
    @SerializedName("backordered"           ) var backordered       : Boolean?              = null,
    @SerializedName("low_stock_amount"      ) var lowStockAmount    : String?               = null,
    @SerializedName("weight"                ) var weight            : String?               = null,
    @SerializedName("dimensions"            ) var dimensions        : Dimensions?           = Dimensions(),
    @SerializedName("shipping_class"        ) var shippingClass     : String?               = null,
    @SerializedName("shipping_class_id"     ) var shippingClassId   : Int?                  = null,
    @SerializedName("image"                 ) var image             : Image?                = Image(),
    @SerializedName("attributes"            ) var attributes        : ArrayList<Attributes> = arrayListOf(),
    @SerializedName("menu_order"            ) var menuOrder         : Int?                  = null,
    @SerializedName("meta_data"             ) var metaData          : ArrayList<MetaData>   = arrayListOf(),
    @SerializedName("name"                  ) var name              : String?               = null,
    @SerializedName("parent_id"             ) var parentId          : Int?                  = null,
    @SerializedName("_links"                ) var Links             : Links?                = Links()

)