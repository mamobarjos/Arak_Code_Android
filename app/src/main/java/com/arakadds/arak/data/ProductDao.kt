package com.arakadds.arak.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getCartProducts(): List<ArakProduct>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCartCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ArakProduct)

    @Update
    suspend fun updateProduct(product: ArakProduct)

    @Delete
    suspend fun deleteProduct(product: ArakProduct)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("SELECT * FROM products WHERE id = :id ")
    suspend fun getProductByIdAndVariant(id: Int): ArakProduct?
}
