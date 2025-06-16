package com.example.medicineremindernew.ui.data.local

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.model.Reminder
import com.example.medicineremindernew.ui.data.model.User

@Database(
    entities = [Obat::class, Lansia::class, Reminder::class,User::class], // ✅ Tambahkan Lansia & Reminder
    version = 2,
    exportSchema = false
)

@TypeConverters(com.example.medicineremindernew.ui.data.model.Converters::class) // ✅ Tambahkan ini
abstract class ObatDatabase : RoomDatabase() {
    abstract fun obatDao(): ObatDao
    abstract fun lansiaDao(): LansiaDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: ObatDatabase? = null

        fun getDatabase(context: Application): ObatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ObatDatabase::class.java,
                    "obat_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }
}