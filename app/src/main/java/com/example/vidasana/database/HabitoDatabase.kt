package com.example.vidasana.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vidasana.models.Habito

@Database(entities = [Habito::class], version = 2)
abstract class HabitoDatabase : RoomDatabase() {
    abstract fun habitoDao(): HabitoDao

    companion object {
        @Volatile private var INSTANCE: HabitoDatabase? = null

        fun getDatabase(context: Context): HabitoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitoDatabase::class.java,
                    "habitos_db"
                )
                    .fallbackToDestructiveMigration() // 🔴 BORRA datos al migrar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
