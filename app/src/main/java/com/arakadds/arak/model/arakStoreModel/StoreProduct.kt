package com.arakadds.arak.model.arakStoreModel

import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreProduct(


    @SerializedName("data") var data: ArrayList<StoreProductData> = arrayListOf(),
    @SerializedName("extra") var extra: ArrayList<String> = arrayListOf()

) : BaseResponse()

data class Dimensions(
    @SerializedName("length") var length: String? = null,
    @SerializedName("width") var width: String? = null,
    @SerializedName("height") var height: String? = null
)


data class Categories(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null
)


data class Images(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("date_created") var dateCreated: String? = null,
    @SerializedName("date_created_gmt") var dateCreatedGmt: String? = null,
    @SerializedName("date_modified") var dateModified: String? = null,
    @SerializedName("date_modified_gmt") var dateModifiedGmt: String? = null,
    @SerializedName("src") var src: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("alt") var alt: String? = null

)

data class Attributes(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("position") var position: Int? = null,
    @SerializedName("visible") var visible: Boolean? = null,
    @SerializedName("variation") var variation: Boolean? = null,
    @SerializedName("options") var options: ArrayList<String> = arrayListOf()

)

data class MetaData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("key") var key: String? = null,
    @SerializedName("value") var value: String? = null

)


data class StoreProductData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("permalink") var permalink: String? = null,
    @SerializedName("date_created") var dateCreated: String? = null,
    @SerializedName("date_created_gmt") var dateCreatedGmt: String? = null,
    @SerializedName("date_modified") var dateModified: String? = null,
    @SerializedName("date_modified_gmt") var dateModifiedGmt: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("featured") var featured: Boolean? = null,
    @SerializedName("catalog_visibility") var catalogVisibility: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("short_description") var shortDescription: String? = null,
    @SerializedName("sku") var sku: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("regular_price") var regularPrice: String? = null,
    @SerializedName("sale_price") var salePrice: String? = null,
    @SerializedName("date_on_sale_from") var dateOnSaleFrom: String? = null,
    @SerializedName("date_on_sale_from_gmt") var dateOnSaleFromGmt: String? = null,
    @SerializedName("date_on_sale_to") var dateOnSaleTo: String? = null,
    @SerializedName("date_on_sale_to_gmt") var dateOnSaleToGmt: String? = null,
    @SerializedName("on_sale") var onSale: Boolean? = null,
    @SerializedName("purchasable") var purchasable: Boolean? = null,
    @SerializedName("total_sales") var totalSales: Int? = null,
    @SerializedName("virtual") var virtual: Boolean? = null,
    @SerializedName("downloadable") var downloadable: Boolean? = null,
    @SerializedName("downloads") var downloads: ArrayList<String> = arrayListOf(),
    @SerializedName("download_limit") var downloadLimit: Int? = null,
    @SerializedName("download_expiry") var downloadExpiry: Int? = null,
    @SerializedName("external_url") var externalUrl: String? = null,
    @SerializedName("button_text") var buttonText: String? = null,
    @SerializedName("tax_status") var taxStatus: String? = null,
    @SerializedName("tax_class") var taxClass: String? = null,
    @SerializedName("manage_stock") var manageStock: Boolean? = null,
    @SerializedName("stock_quantity") var stockQuantity: String? = null,
    @SerializedName("backorders") var backorders: String? = null,
    @SerializedName("backorders_allowed") var backordersAllowed: Boolean? = null,
    @SerializedName("backordered") var backordered: Boolean? = null,
    @SerializedName("low_stock_amount") var lowStockAmount: String? = null,
    @SerializedName("sold_individually") var soldIndividually: Boolean? = null,
    @SerializedName("weight") var weight: String? = null,
    @SerializedName("dimensions") var dimensions: Dimensions? = Dimensions(),
    @SerializedName("shipping_required") var shippingRequired: Boolean? = null,
    @SerializedName("shipping_taxable") var shippingTaxable: Boolean? = null,
    @SerializedName("shipping_class") var shippingClass: String? = null,
    @SerializedName("shipping_class_id") var shippingClassId: Int? = null,
    @SerializedName("reviews_allowed") var reviewsAllowed: Boolean? = null,
    @SerializedName("average_rating") var averageRating: String? = null,
    @SerializedName("rating_count") var ratingCount: Int? = null,
    @SerializedName("upsell_ids") var upsellIds: ArrayList<Int> = arrayListOf(),
    @SerializedName("cross_sell_ids") var crossSellIds: ArrayList<Int> = arrayListOf(),
    @SerializedName("parent_id") var parentId: Int? = null,
    @SerializedName("purchase_note") var purchaseNote: String? = null,
    @SerializedName("categories") var categories: ArrayList<Categories> = arrayListOf(),
    @SerializedName("tags") var tags: ArrayList<String> = arrayListOf(),
    @SerializedName("images") var images: ArrayList<Images> = arrayListOf(),
    @SerializedName("attributes") var attributes: ArrayList<Attributes> = arrayListOf(),
    @SerializedName("default_attributes") var defaultAttributes: ArrayList<String> = arrayListOf(),
    @SerializedName("variations") var variations: ArrayList<Int> = arrayListOf(),
    @SerializedName("grouped_products") var groupedProducts: ArrayList<Int> = arrayListOf(),
    @SerializedName("menu_order") var menuOrder: Int? = null,
    @SerializedName("price_html") var priceHtml: String? = null,
    @SerializedName("related_ids") var relatedIds: ArrayList<Int> = arrayListOf(),
    @SerializedName("meta_data") var metaData: ArrayList<MetaData> = arrayListOf(),
    @SerializedName("stock_status") var stockStatus: String? = null,
    @SerializedName("has_options") var hasOptions: Boolean? = null,
    @SerializedName("post_password") var postPassword: String? = null,
    @SerializedName("global_unique_id") var globalUniqueId: String? = null,
    @SerializedName("jetpack_sharing_enabled") var jetpackSharingEnabled: Boolean? = null,
    @SerializedName("_links") var links: Links? = Links()
) : Serializable /*: Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readValue(Int::class.java.classLoader) as? Int,
        name = parcel.readString(),
        slug = parcel.readString(),
        permalink = parcel.readString(),
        dateCreated = parcel.readString(),
        dateCreatedGmt = parcel.readString(),
        dateModified = parcel.readString(),
        dateModifiedGmt = parcel.readString(),
        type = parcel.readString(),
        status = parcel.readString(),
        featured = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        catalogVisibility = parcel.readString(),
        description = parcel.readString(),
        shortDescription = parcel.readString(),
        sku = parcel.readString(),
        price = parcel.readString(),
        regularPrice = parcel.readString(),
        salePrice = parcel.readString(),
        dateOnSaleFrom = parcel.readString(),
        dateOnSaleFromGmt = parcel.readString(),
        dateOnSaleTo = parcel.readString(),
        dateOnSaleToGmt = parcel.readString(),
        onSale = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        purchasable = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        totalSales = parcel.readValue(Int::class.java.classLoader) as? Int,
        virtual = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        downloadable = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        downloads = parcel.createStringArrayList() ?: arrayListOf(),
        downloadLimit = parcel.readValue(Int::class.java.classLoader) as? Int,
        downloadExpiry = parcel.readValue(Int::class.java.classLoader) as? Int,
        externalUrl = parcel.readString(),
        buttonText = parcel.readString(),
        taxStatus = parcel.readString(),
        taxClass = parcel.readString(),
        manageStock = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        stockQuantity = parcel.readString(),
        backorders = parcel.readString(),
        backordersAllowed = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        backordered = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        lowStockAmount = parcel.readString(),
        soldIndividually = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        weight = parcel.readString(),
        dimensions = parcel.readParcelable(Dimensions::class.java.classLoader),
        shippingRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        shippingTaxable = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        shippingClass = parcel.readString(),
        shippingClassId = parcel.readValue(Int::class.java.classLoader) as? Int,
        reviewsAllowed = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        averageRating = parcel.readString(),
        ratingCount = parcel.readValue(Int::class.java.classLoader) as? Int,
        upsellIds = parcel.createIntArray()?.toCollection(ArrayList()) ?: arrayListOf(),
        crossSellIds = parcel.createIntArray()?.toCollection(ArrayList()) ?: arrayListOf(),
        parentId = parcel.readValue(Int::class.java.classLoader) as? Int,
        purchaseNote = parcel.readString(),
        categories = parcel.createTypedArrayList(Categories.CREATOR) ?: arrayListOf(),
        tags = parcel.createStringArrayList() ?: arrayListOf(),
        images = parcel.createTypedArrayList(Images.CREATOR) ?: arrayListOf(),
        attributes = parcel.createTypedArrayList(Attributes.CREATOR) ?: arrayListOf(),
        defaultAttributes = parcel.createStringArrayList() ?: arrayListOf(),
        variations = parcel.createIntArray()?.toCollection(ArrayList()) ?: arrayListOf(),
        groupedProducts = parcel.createIntArray()?.toCollection(ArrayList()) ?: arrayListOf(),
        menuOrder = parcel.readValue(Int::class.java.classLoader) as? Int,
        priceHtml = parcel.readString(),
        relatedIds = parcel.createIntArray()?.toCollection(ArrayList()) ?: arrayListOf(),
        metaData = parcel.createTypedArrayList(MetaData.CREATOR) ?: arrayListOf(),
        stockStatus = parcel.readString(),
        hasOptions = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        postPassword = parcel.readString(),
        globalUniqueId = parcel.readString(),
        jetpackSharingEnabled = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        links = parcel.readParcelable(Links::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(slug)
        parcel.writeString(permalink)
        parcel.writeString(dateCreated)
        parcel.writeString(dateCreatedGmt)
        parcel.writeString(dateModified)
        parcel.writeString(dateModifiedGmt)
        parcel.writeString(type)
        parcel.writeString(status)
        parcel.writeValue(featured)
        parcel.writeString(catalogVisibility)
        parcel.writeString(description)
        parcel.writeString(shortDescription)
        parcel.writeString(sku)
        parcel.writeString(price)
        parcel.writeString(regularPrice)
        parcel.writeString(salePrice)
        parcel.writeString(dateOnSaleFrom)
        parcel.writeString(dateOnSaleFromGmt)
        parcel.writeString(dateOnSaleTo)
        parcel.writeString(dateOnSaleToGmt)
        parcel.writeValue(onSale)
        parcel.writeValue(purchasable)
        parcel.writeValue(totalSales)
        parcel.writeValue(virtual)
        parcel.writeValue(downloadable)
        parcel.writeStringList(downloads)
        parcel.writeValue(downloadLimit)
        parcel.writeValue(downloadExpiry)
        parcel.writeString(externalUrl)
        parcel.writeString(buttonText)
        parcel.writeString(taxStatus)
        parcel.writeString(taxClass)
        parcel.writeValue(manageStock)
        parcel.writeString(stockQuantity)
        parcel.writeString(backorders)
        parcel.writeValue(backordersAllowed)
        parcel.writeValue(backordered)
        parcel.writeString(lowStockAmount)
        parcel.writeValue(soldIndividually)
        parcel.writeString(weight)
        parcel.writeParcelable(dimensions, flags)
        parcel.writeValue(shippingRequired)
        parcel.writeValue(shippingTaxable)
        parcel.writeString(shippingClass)
        parcel.writeValue(shippingClassId)
        parcel.writeValue(reviewsAllowed)
        parcel.writeString(averageRating)
        parcel.writeValue(ratingCount)
        parcel.writeValue(parentId)
        parcel.writeString(purchaseNote)
        parcel.writeTypedList(categories)
        parcel.writeStringList(tags)
        parcel.writeTypedList(images)
        parcel.writeTypedList(attributes)
        parcel.writeStringList(defaultAttributes)
        parcel.writeIntArray(variations.toIntArray())
        parcel.writeIntArray(groupedProducts.toIntArray())
        parcel.writeValue(menuOrder)
        parcel.writeString(priceHtml)
        parcel.writeIntArray(relatedIds.toIntArray())
        parcel.writeTypedList(metaData)
        parcel.writeString(stockStatus)
        parcel.writeValue(hasOptions)
        parcel.writeString(postPassword)
        parcel.writeString(globalUniqueId)
        parcel.writeValue(jetpackSharingEnabled)
        parcel.writeParcelable(links, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreProductData> {
        override fun createFromParcel(parcel: Parcel): StoreProductData {
            return StoreProductData(parcel)
        }

        override fun newArray(size: Int): Array<StoreProductData?> {
            return arrayOfNulls(size)
        }
    }
}
*/