package com.example.medicineremindernew.ui.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.model.Reminder

@Database(
    entities = [Obat::class, Lansia::class, Reminder::class], // ✅ Tambahkan Lansia & Reminder
    version = 1,
    exportSchema = false
)

@TypeConverters(com.example.medicineremindernew.ui.data.model.Converters::class) // ✅ Tambahkan ini
abstract class ObatDatabase : RoomDatabase() {
    abstract fun obatDao(): ObatDao
    abstract fun lansiaDao(): LansiaDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: ObatDatabase? = null

        fun getDatabase(context: Context): ObatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ObatDatabase::class.java,
                    "obat_database"
                )
                    .fallbackToDestructiveMigration() // ✅ Tambahkan agar error schema lama hilang
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}