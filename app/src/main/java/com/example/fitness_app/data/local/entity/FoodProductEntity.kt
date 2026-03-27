package com.example.fitness_app.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_products")
data class FoodProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val kcal100: Int,
    val kcal200: Int,
    val kcal300: Int
)