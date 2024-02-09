package com.example.wodrunapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Mouvements(
    val id: Int = 0,
    val name: String? = null,
    val type: String? = null,
    val image: String? = null,
    val video: String? = null,
)