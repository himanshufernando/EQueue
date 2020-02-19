package com.project.himanshu.equeue.data.db

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase



@Database(entities = [OriginalTickets::class], version = 1, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {

    abstract fun originalDao(): OriginalDao
    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "equeu_data_base")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {

                        println("ssssssssssss on cretare")
                        super.onCreate(db)
                    }
                })
                .build()
        }
    }
}