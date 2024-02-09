package com.example.wodrunapp.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE mouvements (id INTERGER PRIMARY KEY," +
                "personnalRecord TEXT)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS mouvements"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_NAME = "Wod&Run.db"
        const val DATABASE_VERSION = 1
    }

}