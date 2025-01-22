package com.arakadds.arak.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class ArakProduct(
    @PrimaryKey val id: Int,
    var quantity: Int?,
    var image:String,
    var productName:String,
    var price:String,
    @Embedded var selectedVariant: ProductVariant? // Embedded if it's a nested object
)

@Entity(tableName = "variants")
data class ProductVariant(
    @PrimaryKey val idVariant: Int,
    val name: String
)
