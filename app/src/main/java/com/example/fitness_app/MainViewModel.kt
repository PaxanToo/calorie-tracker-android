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
                        FoodProductEntity(1, "Куриная грудка", 165, 330, 495),
                        FoodProductEntity(2, "Яблоко", 52, 104, 156),
                        FoodProductEntity(3, "Орехи", 600, 1200, 1800),
                        FoodProductEntity(4, "Сыр", 350, 700, 1050),
                        FoodProductEntity(5, "Хлеб", 250, 500, 750)
                    )
                )
            }
        }
    }
}