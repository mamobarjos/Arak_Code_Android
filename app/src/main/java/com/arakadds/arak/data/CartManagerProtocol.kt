package com.arakadds.arak.data

interface CartManagerProtocol {
    suspend fun clearCart()
    suspend fun getCartCount(): Int
    suspend fun addProduct(product: ArakProduct, variant: ProductVariant?)
    suspend fun insertProduct(product: ArakProduct, variant: ProductVariant?)
    suspend fun deleteProduct(product: ArakProduct)
    suspend fun deleteAllProducts()
    suspend fun increaseProductQuantity(product: ArakProduct)
    suspend fun decreaseProductQuantity(product: ArakProduct)
    suspend fun getCartProducts(): List<ArakProduct>
    suspend fun getProductQuantity(id: Int, variant: ProductVariant?): Int
}