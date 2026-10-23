package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ProjectDao
import com.example.data.local.entity.ApiSettingsEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.CustomModelEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity

@Database(
    entities = [
        ProjectEntity::class,
        ProjectFileEntity::class,
        ChatMessageEntity::class,
        ApiSettingsEntity::class,
        CustomModelEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hazon_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
