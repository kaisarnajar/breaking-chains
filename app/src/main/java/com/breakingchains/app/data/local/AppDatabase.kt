package com.breakingchains.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.breakingchains.app.data.local.dao.CallRequestDao
import com.breakingchains.app.data.local.dao.RelapseLogDao
import com.breakingchains.app.data.local.dao.UserDao
import com.breakingchains.app.data.local.entity.CallRequestEntity
import com.breakingchains.app.data.local.entity.RelapseLogEntity
import com.breakingchains.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RelapseLogEntity::class,
        CallRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun relapseLogDao(): RelapseLogDao
    abstract fun callRequestDao(): CallRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "breaking_chains_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
