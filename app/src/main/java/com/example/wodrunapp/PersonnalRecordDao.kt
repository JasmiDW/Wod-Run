package com.example.wodrunapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonnalRecordDao {

    @Query("SELECT personnalRecord FROM PersonnalRecord WHERE mouvementId = :id ORDER BY id DESC LIMIT 1")
    suspend fun getLastPrRecordByMouvementId(id:Int): String?

    @Query("SELECT personnalRecord FROM PersonnalRecord WHERE mouvementId = :id ORDER BY id")
    suspend fun getPrRecordByMouvementId(id:Int): String?

    @Query("SELECT * FROM PersonnalRecord WHERE mouvementId = :id ORDER BY id")
    suspend fun getPrRecordsByMouvementId(id:Int): List<PersonnalRecord>?

    @Insert
    suspend fun insertAll(vararg  personnalRecords: PersonnalRecord)

    @Update
    suspend fun update(  mouvement: PersonnalRecord)

}