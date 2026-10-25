package com.azerostudio.disela.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class DiselaDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: DiselaDatabase? = null

        fun get(context: Context): DiselaDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DiselaDatabase::class.java,
                    "disela.db"
                ).build().also { instance = it }
            }
        }
    }
}
