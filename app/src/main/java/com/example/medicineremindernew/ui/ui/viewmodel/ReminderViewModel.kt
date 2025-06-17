package com.example.medicineremindernew.ui.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.alarm.scheduleAlarm
import com.example.medicineremindernew.ui.data.model.Reminder
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {
    private val now = System.currentTimeMillis()
    private val today = java.sql.Date(now)
    private val currentTime = java.sql.Time(now)

//    val reminderList: StateFlow<List<Reminder>> = flow {
//        val now = java.util.Calendar.getInstance()
//        val currentDate = java.sql.Date(now.time.time)
//        val currentTime = java.sql.Time(now.time.time)
//
//        repository.getAllValidReminder(currentDate, currentTime).collect {
//            emit(it)
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
val reminderList: StateFlow<List<Reminder>> = flow {
    val calendar = java.util.Calendar.getInstance()
    val date = java.sql.Date(calendar.time.time)

    // Set detik dan milidetik ke 0
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    val time = java.sql.Time(calendar.time.time)

    Log.d("ReminderDebug", "MENGAMBIL reminder pakai date=$date dan time=$time")

    repository.getAllValidReminder().collect {
        Log.d("ReminderDebug", "Reminder yang tampil: ${it.size}")
        emit(it)
    }
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
)

    val allReminderRaw: StateFlow<List<Reminder>> = repository.debugGetAll().stateIn(
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
            Log.d("ReminderDebug", "Tanggal: ${reminder.tanggal}, Waktu: ${reminder.waktu}") // ⬅️ log di sini
            Log.d("ReminderDebug", "Menjadwalkan alarm pada ${reminder.waktu} tanggal ${reminder.tanggal}")

            val insertedId = repository.insertAndReturnId(reminder) // ⬅️ pakai repository
            val insertedReminder = reminder.copy(id = insertedId.toInt())
            scheduleAlarm(context, insertedReminder)
            onComplete()
        }
    }

    fun updateAndSchedule(reminder: Reminder, context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            Log.d("ReminderDebug", "Tanggal: ${reminder.tanggal}, Waktu: ${reminder.waktu}") // ⬅️ log di sini
            Log.d("ReminderDebug", "Menjadwalkan alarm pada ${reminder.waktu} tanggal ${reminder.tanggal}")
            repository.update(reminder) // Update reminder di database
            scheduleAlarm(context, reminder) // Jadwalkan alarm baru
            onComplete() // Callback jika semua selesai
        }
    }

}









