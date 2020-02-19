package com.project.himanshu.equeue.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface OriginalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOriginal(codes: OriginalTickets)

    @Query("SELECT * from thousand")
    fun getFriendsList(): LiveData<List<OriginalTickets>>

    @Query("UPDATE thousand SET isupdate = 1 WHERE qrcode = :qr")
     fun updateQR(qr: String)


}