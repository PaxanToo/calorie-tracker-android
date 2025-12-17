package com.example.fitness_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Insert
    fun insertItemDb(item: ItemDb)
    @Query("SELECT * FROM items")
    fun getAllItemDb(): Flow<List<ItemDb>>

}