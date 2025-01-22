package com.arakadds.arak.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class CartManager(context: Context) : CartManagerProtocol {
    private val productDao: ProductDao = AppDatabase.getDatabase(context).productDao()


    override suspend fun getCartCount(): Int = withContext(Dispatchers.IO) {
        val count = productDao.getCartProducts().sumOf { it.quantity ?: 0 }
        Helper.CartItemsCount = count
        withContext(Dispatchers.Main) {
            EventBus.getDefault().post(CartUpdateEvent(count))
        }
        count
    }


    override suspend fun getCartProducts(): List<ArakProduct> = withContext(Dispatchers.IO) {
        productDao.getCartProducts()
    }

    override suspend fun addProduct(product: ArakProduct, variant: ProductVariant?) =
        withContext(Dispatchers.IO) {
            product.selectedVariant = variant
            productDao.insertProduct(product)
            notifyCartUpdate()
        }

    override suspend fun insertProduct(product: ArakProduct, variant: ProductVariant?) =
        withContext(Dispatchers.IO) {
            product.selectedVariant = variant
            productDao.insertProduct(product)
            notifyCartUpdate()
        }
    override suspend fun deleteProduct(product: ArakProduct) {
        productDao.deleteProduct(product)
        notifyCartUpdate()
    }
    override suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
        notifyCartUpdate()
    }

//    override suspend fun deleteProduct(product: ArakProduct, variant: ProductVariant?) = withContext(Dispatchers.IO) {
//        val existingProduct = productDao.getProductByIdAndVariant(product.id, variant)
//        if (existingProduct != null) {
//            productDao.deleteProduct(existingProduct)
//        }
//        notifyCartUpdate()
//    }

    override suspend fun increaseProductQuantity(product: ArakProduct) =
        withContext(Dispatchers.IO) {
            val existingProduct = productDao.getProductByIdAndVariant(product.id)
            if (existingProduct != null) {
                existingProduct.quantity = (existingProduct.quantity ?: 0) + 1
                productDao.updateProduct(existingProduct)
            } else {
                product.quantity = 1
                productDao.insertProduct(product)
            }
            notifyCartUpdate()
        }
    override suspend fun decreaseProductQuantity(product: ArakProduct) =
        withContext(Dispatchers.IO) {
            val existingProduct = productDao.getProductByIdAndVariant(product.id)
            if (existingProduct != null) {
                existingProduct.quantity = (existingProduct.quantity ?: 0) - 1
                if (existingProduct.quantity!! <= 0) {
                    productDao.deleteProduct(existingProduct)
                } else {
                    productDao.updateProduct(existingProduct)
                }
            }
            notifyCartUpdate()
        }
    override suspend fun getProductQuantity(id: Int, variant: ProductVariant?): Int =
        withContext(Dispatchers.IO) {
            val product = productDao.getProductByIdAndVariant(id)
            product?.quantity ?: 0
        }

    override suspend fun clearCart() = withContext(Dispatchers.IO) {
        val products = getCartProducts()
        for (product in products) {
            productDao.deleteProduct(product)
        }
        notifyCartUpdate()
    }

    private suspend fun notifyCartUpdate() = withContext(Dispatchers.Main) {
        val totalItems = productDao.getCartProducts().sumOf { it.quantity ?: 0 }
        Helper.CartItemsCount = totalItems
        EventBus.getDefault().post(CartUpdateEvent(totalItems))
    }



}

object Helper {
    var CartItemsCount: Int = 0
    // Add other cart-related utilities or global state as needed
}