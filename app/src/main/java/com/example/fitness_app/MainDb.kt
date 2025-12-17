package com.example.fitness_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database (entities = [ItemDb::class], version = 1) // если будет много таблиц то (entities = [ItemDb::class, имя::class  и так далее])
abstract class MainDb : RoomDatabase() {

    abstract fun getDao(): Dao
    companion object{
        fun getDb(context: Context): MainDb{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDb::class.java,
                "test.db"
            ).build()
        }
    }
}