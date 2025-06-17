package com.example.medicineremindernew.ui.data.repository

import com.example.medicineremindernew.ui.data.local.ReminderDao
import com.example.medicineremindernew.ui.data.model.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {
    val getAllReminder: Flow<List<Reminder>> = dao.getAll()
    suspend fun insert(reminder: Reminder) = dao.insert(reminder)
    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
    fun getReminderById(id: Int): Flow<Reminder?> = dao.getReminderById(id)
    suspend fun update(reminder: Reminder) = dao.update(reminder)

}
