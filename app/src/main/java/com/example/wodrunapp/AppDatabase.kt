package com.example.wodrunapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PersonnalRecord::class], version = 1)
 abstract class AppDatabase : RoomDatabase(){
     abstract fun personnalRecordDao(): PersonnalRecordDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE CourseEntity ADD COLUMN name TEXT NOT NULL DEFAULT ''")
    }
}

@Database(entities = [CourseEntity::class], version = 2)
abstract class AppDatabaseRun : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}



