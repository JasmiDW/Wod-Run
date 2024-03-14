package com.example.wodrunapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CourseDao {
    @Query("SELECT * FROM courseentity ORDER BY id DESC")
    fun getAll(): LiveData<List<CourseEntity>>

    @Insert
    fun insertAll(vararg courses: CourseEntity)

    @Query("DELETE FROM courseentity WHERE id = :courseId")
    suspend fun delete(courseId: Int)

    @Query("DELETE  FROM courseentity")
    suspend fun deleteAllCourses()
}