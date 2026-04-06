package com.example.fitness_app.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_products")
data class FoodProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val kcalPer100: Int,
    val proteinPer100: Int,
    val fatPer100: Int,
    val carbsPer100: Int
)