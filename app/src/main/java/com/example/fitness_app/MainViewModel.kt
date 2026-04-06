package com.example.fitness_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_app.data.local.db.AppDatabase
import com.example.fitness_app.data.local.entity.FoodProductEntity
import com.example.fitness_app.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDb(application)
    private val dao = db.getDao()

    val items = dao.getAllItemDb()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertItem(item: ItemEntity) {
        viewModelScope.launch {
            dao.insertItemDb(item)
        }
    }

    val foodProducts = dao.getAllFoodProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun ensureFoodProductsSeeded() {
        viewModelScope.launch {
            if (dao.foodProductsCount() == 0) {
                dao.insertFoodProducts(
                    listOf(
                        FoodProductEntity(
                            id = 1,
                            name = "Куриная грудка",
                            kcalPer100 = 165,
                            proteinPer100 = 31,
                            fatPer100 = 4,
                            carbsPer100 = 0
                        ),
                        FoodProductEntity(
                            id = 2,
                            name = "Яблоко",
                            kcalPer100 = 52,
                            proteinPer100 = 0,
                            fatPer100 = 0,
                            carbsPer100 = 14
                        ),
                        FoodProductEntity(
                            id = 3,
                            name = "Орехи",
                            kcalPer100 = 600,
                            proteinPer100 = 20,
                            fatPer100 = 53,
                            carbsPer100 = 13
                        ),
                        FoodProductEntity(
                            id = 4,
                            name = "Сыр",
                            kcalPer100 = 350,
                            proteinPer100 = 24,
                            fatPer100 = 27,
                            carbsPer100 = 1
                        ),
                        FoodProductEntity(
                            id = 5,
                            name = "Хлеб",
                            kcalPer100 = 250,
                            proteinPer100 = 8,
                            fatPer100 = 3,
                            carbsPer100 = 49
                        )
                    )
                )
            }
        }
    }
}