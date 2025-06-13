package com.example.medicineremindernew.ui.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medicineremindernew.ui.data.model.Obat

@Database(entities = [Obat::class], version = 1, exportSchema = false)
abstract class ObatDatabase : RoomDatabase() {
    abstract fun obatDao(): ObatDao

    companion object {
        @Volatile private var INSTANCE: ObatDatabase? = null

        fun getDatabase(context: Context): ObatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ObatDatabase::class.java,
                    "obat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
