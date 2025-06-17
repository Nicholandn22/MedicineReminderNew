package com.example.medicineremindernew.ui.data.repository

import android.icu.util.Calendar
import android.util.Log
import com.example.medicineremindernew.ui.data.local.ReminderDao
import com.example.medicineremindernew.ui.data.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.sql.Date

class ReminderRepository(private val dao: ReminderDao) {
//    fun getUpcomingReminders(): Flow<List<Reminder>> {
//        val now = Calendar.getInstance()
//
//        // Zeroing detik dan milidetik
//        now.set(Calendar.SECOND, 0)
//        now.set(Calendar.MILLISECOND, 0)
//
//        val currentDate = java.sql.Date(now.time.time)
//        val currentTime = java.sql.Time(now.time.time)
//
//        Log.d("ReminderDebug", "QUERY Reminder pakai Tanggal: $currentDate, Waktu: $currentTime")
//
//        return dao.getAll(currentDate, currentTime)
//    }


    suspend fun insert(reminder: Reminder) = dao.insert(reminder)
    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
    fun getReminderById(id: Int): Flow<Reminder?> = dao.getReminderById(id)
    suspend fun update(reminder: Reminder) = dao.update(reminder)

    suspend fun insertAndReturnId(reminder: Reminder): Long {
        return dao.insert(reminder)
    }

    fun getAllValidReminder(): Flow<List<Reminder>> {
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)

        val currentDate = Date(now.time.time)

        Log.d("ReminderDebug", "QUERY pakai currentDate=$currentDate")
        return dao.getAll(currentDate)
    }


    fun debugGetAll(): Flow<List<Reminder>> = dao.getAllRemindersRaw()




}
