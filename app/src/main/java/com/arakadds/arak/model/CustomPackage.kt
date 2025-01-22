package com.arakadds.arak.model

import com.google.gson.annotations.SerializedName

data class CustomPackage(

    @SerializedName("status_code") var statusCode: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: CustomPackageData? = CustomPackageData(),
    @SerializedName("extra") var extra: ArrayList<String> = arrayListOf()

)

data class CustomReachs(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("value") var value: Int? = null,
    @SerializedName("second_price") var secondPrice: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)

data class CustomSeconds(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("value") var value: Int? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)

data class CustomGenders(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)

data class CustomAges(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("age") var age: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)


data class Country(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name_en") var nameEn: String? = null,
    @SerializedName("name_ar") var nameAr: String? = null

)

data class CustomCountries(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("country_id") var countryId: Int? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("country") var country: Country? = Country()

)

data class City(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name_en") var nameEn: String? = null,
    @SerializedName("name_ar") var nameAr: String? = null

)

data class CustomCities(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("city_id") var cityId: Int? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("city") var city: City? = City()

)

data class CustomImgs(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("city_id") var cityId: Int? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("city") var city: City? = City()

)


data class CustomPackageData(

    @SerializedName("customImgs") var customImgs: ArrayList<CustomImgs> = arrayListOf(),
    @SerializedName("customReachs") var customReachs: ArrayList<CustomReachs> = arrayListOf(),
    @SerializedName("customSeconds") var customSeconds: ArrayList<CustomSeconds> = arrayListOf(),
    @SerializedName("customGenders") var customGenders: ArrayList<CustomGenders> = arrayListOf(),
    @SerializedName("customAges") var customAges: ArrayList<CustomAges> = arrayListOf(),
    @SerializedName("customCountries") var customCountries: ArrayList<CustomCountries> = arrayListOf(),
    @SerializedName("customCities") var customCities: ArrayList<CustomCities> = arrayListOf()

)