package com.example.fitness_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "items")
data class ItemDb (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "weight")
    var weight: Int,
    @ColumnInfo(name = "height")
    var height: String


        )
