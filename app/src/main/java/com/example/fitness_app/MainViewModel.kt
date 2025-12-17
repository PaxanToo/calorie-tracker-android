package com.example.fitness_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
}
