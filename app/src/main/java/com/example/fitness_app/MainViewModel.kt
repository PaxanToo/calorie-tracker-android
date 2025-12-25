package com.example.fitness_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.fitness_app.food.FoodProductDb

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MainDb.getDb(application)
    private val dao = db.getDao()



    val items = dao.getAllItemDb()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun insertItem(item: ItemDb) {
        viewModelScope.launch {
            dao.insertItemDb(item)
        }
    }


    val foodProducts = dao.getAllFoodProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun ensureFoodProductsSeeded() {
        viewModelScope.launch {
            if (dao.foodProductsCount() == 0) {
                dao.insertFoodProducts(
                    listOf(
                        FoodProductDb(1, "Куриная грудка", 165, 330, 495),
                        FoodProductDb(2, "Яблоко", 52, 104, 156),
                        FoodProductDb(3, "Орехи", 600, 1200, 1800),
                        FoodProductDb(4, "Сыр", 350, 700, 1050),
                        FoodProductDb(5, "Хлеб", 250, 500, 750),
                    )
                )
            }
        }
    }





}
