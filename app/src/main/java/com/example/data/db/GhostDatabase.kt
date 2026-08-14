package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AppEntity::class, UserSettingsEntity::class],
    version = 4,
    exportSchema = false
)
abstract class GhostDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: GhostDatabase? = null

        fun getInstance(context: Context): GhostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GhostDatabase::class.java,
                    "ghost_launcher.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
