package com.project.himanshu.equeue.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thousand")
data class OriginalTickets (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id:") val id: Long,
    @ColumnInfo(name = "code_id") val code_id: String,
    @ColumnInfo(name = "qrcode") val qrcode: String,
    @ColumnInfo(name = "tcat") val tcat: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "user") val user: String
){
}