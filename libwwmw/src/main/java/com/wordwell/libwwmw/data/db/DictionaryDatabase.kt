package com.wordwell.libwwmw.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wordwell.libwwmw.data.db.dao.WordDao
import com.wordwell.libwwmw.data.db.entities.WordConverters
import com.wordwell.libwwmw.data.db.entities.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WordConverters::class)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        fun getInstance(context: Context): DictionaryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "dictionary.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
} 