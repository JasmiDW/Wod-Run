package com.example.wodrunapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name : String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val distance: Double,
    val time: Long
)