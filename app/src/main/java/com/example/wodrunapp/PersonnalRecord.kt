package com.example.wodrunapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonnalRecord (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val mouvementId: Int = 0,
    @ColumnInfo val personnalRecord : String? = null

)