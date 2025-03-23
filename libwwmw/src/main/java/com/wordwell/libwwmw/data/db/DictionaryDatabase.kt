package com.wordwell.libwwmw.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wordwell.libwwmw.data.db.dao.SetDao
import com.wordwell.libwwmw.data.db.dao.WordDao
import com.wordwell.libwwmw.data.db.entities.WordConverters
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.data.db.entities.SetEntity

// DictionaryDatabase is the Room database for storing dictionary words and sets.
// It provides access to the WordDao and SetDao for performing database operations.
@Database(
    entities = [WordEntity::class, SetEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(WordConverters::class)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun setDao(): SetDao
    companion object {
        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        /**
         * Provides the singleton instance of DictionaryDatabase.
         * If an instance does not exist, it creates one using the provided context.
         * @param context The application context
         * @return The singleton instance of DictionaryDatabase
         */
        fun getInstance(context: Context): DictionaryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "dictionary.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
} 