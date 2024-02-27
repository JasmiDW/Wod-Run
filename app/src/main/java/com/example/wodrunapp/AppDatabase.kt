package com.example.wodrunapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PersonnalRecord::class], version = 1)
 abstract class AppDatabase : RoomDatabase(){
     abstract fun personnalRecordDao(): PersonnalRecordDao
}