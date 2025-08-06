package com.example.medicineremindernew.ui.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.medicineremindernew.ui.alarm.cancelAlarm
import com.example.medicineremindernew.ui.alarm.scheduleAlarm
import com.example.medicineremindernew.ui.data.dao.LocalReminderDao
import com.example.medicineremindernew.ui.data.entity.LocalReminderEntity
import com.example.medicineremindernew.ui.data.model.Reminder
import com.example.medicineremindernew.ui.data.network.NetworkUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.resume

class HybridReminderRepository(
    private val reminderRepository: ReminderRepository,
    private val localDao: LocalReminderDao,
    private val networkUtils: NetworkUtils,
    private val context: Context
) {

    suspend fun addReminder(reminder: Reminder): Boolean {
        return try {
            val fixedReminder = if (reminder.id.isEmpty()) {
                reminder.copy(id = java.util.UUID.randomUUID().toString())  // ✅ Generate UUID jika kosong
            } else {
                reminder
            }

            if (networkUtils.isNetworkAvailable()) {
                // ✅ Paksa Firestore pakai fixedReminder.id sebagai Document ID
                reminderRepository.addReminder(fixedReminder)
                localDao.insertReminder(fixedReminder.toLocalEntity(isSynced = true))
            } else {
                localDao.insertReminder(fixedReminder.toLocalEntity(isSynced = false))
            }

            true
        } catch (e: Exception) {
            Log.e("HybridReminderRepo", "Add failed: ${e.message}")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateReminder(reminder: Reminder): Boolean {
        return try {
            Log.d("HybridReminderRepo", "=== STARTING UPDATE FOR ${reminder.id} ===")
            Log.d("HybridReminderRepo", "New time: ${reminder.tanggal} ${reminder.waktu}")

            // STEP 1: Cancel alarm lama
            cancelAlarm(context, reminder.id)

            // STEP 2: Update data di Firestore & Local DB
            if (networkUtils.isNetworkAvailable()) {
                reminderRepository.updateReminder(reminder)  // ✅ Update Firestore Document pakai reminder.id
                localDao.updateReminder(reminder.toLocalEntity(isSynced = true))
            } else {
                val localData = localDao.getAllReminders().find { it.id == reminder.id }
                if (localData != null) {
                    localDao.updateReminder(reminder.toLocalEntity(isSynced = false))
                }
            }

            // STEP 3: Safety cancel (optional tapi biarin aja bro, bagus preventif)
            cancelAlarm(context, reminder.id)

            // STEP 4: Delay sedikit biar cancel settle
            kotlinx.coroutines.delay(500)

            // STEP 5: Schedule alarm baru
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val localDateTime = java.time.LocalDateTime.parse("${reminder.tanggal} ${reminder.waktu}", formatter)
            val timeMillis = localDateTime
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val currentTime = System.currentTimeMillis()
            if (timeMillis > currentTime) {
                scheduleAlarm(context, reminder.id, timeMillis)
                Log.d("HybridReminderRepo", "✅ NEW ALARM SCHEDULED for ${Date(timeMillis)}")
            } else {
                Log.d("HybridReminderRepo", "⚠️ ALARM TIME IN PAST - NOT SCHEDULED")
            }

            Log.d("HybridReminderRepo", "=== UPDATE COMPLETED SUCCESSFULLY ===")
            true
        } catch (e: Exception) {
            Log.e("HybridReminderRepo", "=== UPDATE FAILED ===", e)
            false
        }
    }

    suspend fun deleteReminder(id: String): Boolean {
        return try {
            // STEP 1: Cancel alarm dulu
            cancelAlarm(context, id)

            // STEP 2: Delete di Firestore & Local DB
            if (networkUtils.isNetworkAvailable()) {
                reminderRepository.deleteReminder(id)
                localDao.deleteReminder(id)
            } else {
                localDao.deleteReminder(id)
            }
            true
        } catch (e: Exception) {
            Log.e("HybridReminderRepo", "Delete failed: ${e.message}")
            false
        }
    }

    suspend fun getAllReminders(): List<Reminder> {
        return if (networkUtils.isNetworkAvailable()) {
            try {
                reminderRepository.getAllReminders()
            } catch (e: Exception) {
                Log.e("HybridReminderRepo", "Get remote failed: ${e.message}")
                getLocalReminders()
            }
        } else {
            getLocalReminders()
        }
    }

    private suspend fun getLocalReminders(): List<Reminder> {
        return localDao.getAllReminders().map { it.toDomainModel() }
    }

    suspend fun syncPendingData() {
        if (!networkUtils.isNetworkAvailable()) return

        val unsynced = localDao.getUnsyncedReminders()
        unsynced.forEach { entity ->
            val reminder = entity.toDomainModel()
            try {
                reminderRepository.addReminder(reminder)  // ✅ Ensure Firestore doc ID = reminder.id
                localDao.markAsSynced(entity.id)
            } catch (e: Exception) {
                Log.e("HybridReminderRepo", "Sync failed for ${entity.id}: ${e.message}")
            }
        }
    }

    // Mapping Extensions
    private fun Reminder.toLocalEntity(isSynced: Boolean): LocalReminderEntity {
        return LocalReminderEntity(
            id = this.id,
            obatId = this.obatId,
            lansiaId = this.lansiaId,
            tanggal = this.tanggal,
            waktu = this.waktu,
            pengulangan = this.pengulangan,
            isSynced = isSynced
        )
    }

    private fun LocalReminderEntity.toDomainModel(): Reminder {
        return Reminder(
            id = this.id,
            obatId = this.obatId,
            lansiaId = this.lansiaId,
            tanggal = this.tanggal,
            waktu = this.waktu,
            pengulangan = this.pengulangan
        )
    }
}

