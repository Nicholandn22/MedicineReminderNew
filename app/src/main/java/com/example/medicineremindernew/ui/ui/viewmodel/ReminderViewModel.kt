package com.example.medicineremindernew.ui.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.alarm.scheduleAlarm
import com.example.medicineremindernew.ui.data.model.Reminder
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {
    val reminderList: StateFlow<List<Reminder>> = repository.getAllReminder.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        repository.delete(reminder)
    }

    fun getReminderById(id: Int): Flow<Reminder?> = repository.getReminderById(id)

    fun update(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    fun insertAndSchedule(reminder: Reminder, context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            val insertedId = repository.insertAndReturnId(reminder) // ⬅️ pakai repository
            val insertedReminder = reminder.copy(id = insertedId.toInt())
            scheduleAlarm(context, insertedReminder)
            onComplete()
        }
    }

    fun updateAndSchedule(reminder: Reminder, context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.update(reminder) // Update reminder di database
            scheduleAlarm(context, reminder) // Jadwalkan alarm baru
            onComplete() // Callback jika semua selesai
        }
    }

}









