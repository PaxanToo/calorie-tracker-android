package com.example.fitness_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.fitness_app.food.FoodProductDb


@Dao
interface Dao {
    @Insert
    suspend fun insertItemDb(item: ItemDb)
    @Query("SELECT * FROM items")
    fun getAllItemDb(): Flow<List<ItemDb>>




    @Query("SELECT * FROM food_products")
    fun getAllFoodProducts(): Flow<List<FoodProductDb>>
    @Insert
    suspend fun insertFoodProducts(list: List<FoodProductDb>)
    @Query("SELECT COUNT(*) FROM food_products")
    suspend fun foodProductsCount(): Int





}