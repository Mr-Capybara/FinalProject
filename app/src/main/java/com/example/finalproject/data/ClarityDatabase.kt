package com.example.finalproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class,
        FocusSessionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ClarityDatabase : RoomDatabase() {
    abstract fun clarityDao(): ClarityDao

    companion object {
        @Volatile
        private var instance: ClarityDatabase? = null

        fun get(context: Context): ClarityDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ClarityDatabase::class.java,
                    "clarity.db",
                ).build().also { instance = it }
            }
        }
    }
}
