package com.example.medicineremindernew.ui.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicineremindernew.ui.data.dao.LocalLansiaDao
import com.example.medicineremindernew.ui.data.dao.LocalObatDao
import com.example.medicineremindernew.ui.data.dao.LocalReminderDao
import com.example.medicineremindernew.ui.data.entity.LocalReminderEntity
import com.example.medicineremindernew.ui.data.entity.LocalLansiaEntity
import com.example.medicineremindernew.ui.data.entity.LocalObatEntity

@Database(
    entities = [LocalReminderEntity::class, LocalLansiaEntity::class, LocalObatEntity::class],
    version = 1,
    exportSchema = false
)

abstract class LocalDatabase : RoomDatabase() {

    abstract fun reminderDao(): LocalReminderDao
    abstract fun lansiaDao(): LocalLansiaDao
    abstract fun obatDao(): LocalObatDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "medicine_local_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}