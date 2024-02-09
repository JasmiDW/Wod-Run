package com.example.wodrunapp

import android.app.Application
import androidx.room.Room

class WRApplication : Application() {

    lateinit var currentMouvement: Mouvements

    val personnalRecordDao : PersonnalRecordDao by lazy {
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java, "Wod&Run").build()
        db.personnalRecordDao()
    }
}