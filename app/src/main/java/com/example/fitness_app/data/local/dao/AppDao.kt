package com.example.fitness_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fitness_app.data.local.entity.FoodProductEntity
import com.example.fitness_app.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert
    suspend fun insertItemDb(item: ItemEntity)

    @Query("SELECT * FROM items")
    fun getAllItemDb(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM food_products")
    fun getAllFoodProducts(): Flow<List<FoodProductEntity>>

    @Insert
    suspend fun insertFoodProducts(list: List<FoodProductEntity>)

    @Query("SELECT COUNT(*) FROM food_products")
    suspend fun foodProductsCount(): Int
}