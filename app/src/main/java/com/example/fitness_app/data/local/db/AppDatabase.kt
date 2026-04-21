package com.example.fitness_app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitness_app.data.local.dao.AppDao
import com.example.fitness_app.data.local.entity.FoodProductEntity
import com.example.fitness_app.data.local.entity.ItemEntity

@Database(
    entities = [ItemEntity::class, FoodProductEntity::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDao(): AppDao

    companion object {
        fun getDb(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "test.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}