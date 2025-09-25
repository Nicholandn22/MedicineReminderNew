package com.example.medicineremindernew.ui.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object AlarmUtils {

    /**
     * Menjadwalkan alarm berulang berdasarkan jenis pengulangan
     */
    fun scheduleRecurringReminder(
        context: Context,
        reminderId: String,
        timeInMillis: Long,
        recurrenceType: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("recurrence_type", recurrenceType)
            putExtra("original_time", timeInMillis)
            putExtra("is_recurring", true)
            action = "com.example.medicineremindernew.ALARM"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//        val intervalMillis = when (recurrenceType) {
//            "Harian" -> AlarmManager.INTERVAL_DAY
//            "Mingguan" -> AlarmManager.INTERVAL_DAY * 7
//            "Bulanan" -> AlarmManager.INTERVAL_DAY * 30
//            else -> AlarmManager.INTERVAL_DAY // Default harian
//        }

        try {
            // ✅ Jadwalkan alarm pertama kali
            scheduleAlarmSafely(alarmManager, timeInMillis, pendingIntent)
            Log.d("AlarmUtils", "Initial alarm scheduled for $reminderId with type: $recurrenceType at ${Date(timeInMillis)}")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule recurring alarm", e)
        }
    }

    /**
     * Update alarm ke waktu berikutnya setelah obat diminum
     * Dipanggil setelah user mengkonfirmasi "sudah diminum"
     */
    fun updateToNextAlarmTime(
        context: Context,
        reminderId: String,
        recurrenceType: String,
        currentDateStr: String,  // Format: "2025-07-24"
        currentTimeStr: String   // Format: "15:00"
    ) {
        if (recurrenceType == "Sekali") {
            // Jika hanya sekali, hapus alarm dan jangan buat ulang
            cancelRecurringAlarm(context, reminderId)
            Log.d("AlarmUtils", "One-time alarm completed for $reminderId")
            return
        }

        try {
            // Parse current date dan time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val currentDate = dateFormat.parse(currentDateStr)
            val currentTime = timeFormat.parse(currentTimeStr)

            if (currentDate != null && currentTime != null) {
                // Gabungkan date dan time
                val calendar = Calendar.getInstance()
                calendar.time = currentDate

                val timeCalendar = Calendar.getInstance()
                timeCalendar.time = currentTime

                calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Hitung waktu alarm berikutnya
                val nextAlarmTime = calculateNextAlarmTime(calendar.timeInMillis, recurrenceType)

                // Update ke Firestore
                updateAlarmInFirestore(reminderId, nextAlarmTime, recurrenceType)

                // Jadwalkan alarm berikutnya
                scheduleNextAlarmAfterTaken(context, reminderId, nextAlarmTime, recurrenceType)

                Log.d("AlarmUtils", "Alarm updated to next occurrence for $reminderId: ${Date(nextAlarmTime)}")
            }
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to update alarm to next time", e)
        }
    }

    /**
     * Menghitung waktu alarm berikutnya berdasarkan jenis recurrence
     */
    private fun calculateNextAlarmTime(currentTimeInMillis: Long, recurrenceType: String): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTimeInMillis
        }

        return when (recurrenceType) {
            "Harian" -> {
                // Tambah 1 hari
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            }

            "Mingguan" -> {
                // Tambah 7 hari
                calendar.add(Calendar.DAY_OF_MONTH, 7)
                calendar.timeInMillis
            }

//            "Bulanan" -> {
//                // Tambah 1 bulan
//                val originalDay = calendar.get(Calendar.DAY_OF_MONTH)
//                calendar.add(Calendar.MONTH, 1)
//
//                // Handle case dimana tanggal tidak ada di bulan berikutnya (misal 31 Januari -> 28/29 Februari)
//                val maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//                if (originalDay > maxDayInMonth) {
//                    calendar.set(Calendar.DAY_OF_MONTH, maxDayInMonth)
//                }
//
//                calendar.timeInMillis
//            }

            else -> {
                // Default ke harian jika tidak dikenal
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            }
        }
    }

    /**
     * Update data alarm di Firestore dengan tanggal dan waktu baru
     */
    private fun updateAlarmInFirestore(
        reminderId: String,
        nextTimeInMillis: Long,
        recurrenceType: String
    ) {
        try {
            val db = FirebaseFirestore.getInstance()
            val calendar = Calendar.getInstance().apply {
                timeInMillis = nextTimeInMillis
            }

            // Format tanggal dan waktu untuk Firestore
            val newDate = String.format(
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            val newTime = String.format(
                "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )

            // Update document di Firestore
            val updateData = hashMapOf<String, Any>(
                "tanggal" to newDate,
                "waktu" to newTime,
                "statusIoT" to "ON", // Reset status untuk alarm berikutnya
                "lastUpdated" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            db.collection("reminders").document(reminderId)
                .update(updateData)
                .addOnSuccessListener {
                    Log.d("AlarmUtils", "Firestore updated for $reminderId: $newDate $newTime")
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmUtils", "Failed to update Firestore for $reminderId", e)
                }

        } catch (e: Exception) {
            Log.e("AlarmUtils", "Exception updating Firestore", e)
        }
    }

    /**
     * Jadwalkan alarm berikutnya setelah obat diminum
     */
    private fun scheduleNextAlarmAfterTaken(
        context: Context,
        reminderId: String,
        nextTimeInMillis: Long,
        recurrenceType: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("reminderId", reminderId)
            putExtra("recurrence_type", recurrenceType)
            putExtra("original_time", nextTimeInMillis)
            putExtra("is_recurring", true)
            action = "com.example.medicineremindernew.ALARM"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            scheduleAlarmSafely(alarmManager, nextTimeInMillis, pendingIntent)
            Log.d("AlarmUtils", "Next alarm scheduled after medicine taken: ${Date(nextTimeInMillis)}")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule next alarm after taken", e)
        }
    }

    /**
     * Menjadwalkan alarm berikutnya setelah alarm saat ini berbunyi
     * Dipanggil dari AlarmReceiver - hanya untuk fallback
     */
    fun scheduleNextRecurringAlarm(
        context: Context,
        reminderId: String,
        originalTimeInMillis: Long,
        recurrenceType: String
    ) {
        // ✅ PENTING: Ini hanya fallback jika somehow alarm tidak diupdate via updateToNextAlarmTime
        // Normalnya, alarm diupdate saat user menekan "sudah diminum"

        if (recurrenceType == "Sekali") {
            Log.d("AlarmUtils", "One-time alarm, no need to schedule next")
            return
        }

        val nextTimeInMillis = calculateNextAlarmTime(originalTimeInMillis, recurrenceType)
        scheduleNextAlarmAfterTaken(context, reminderId, nextTimeInMillis, recurrenceType)
    }

    /**
     * Membatalkan alarm berulang
     */
    fun cancelRecurringAlarm(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.medicineremindernew.ALARM"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d("AlarmUtils", "Recurring alarm cancelled for: $reminderId")
    }

    /**
     * Menjadwalkan alarm sekali saja (non-recurring)
     */
    fun scheduleOneTimeReminder(
        context: Context,
        reminderId: String,
        timeInMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("reminderId", reminderId)
            putExtra("recurrence_type", "Sekali")
            putExtra("is_recurring", false)
            action = "com.example.medicineremindernew.ALARM"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            scheduleAlarmSafely(alarmManager, timeInMillis, pendingIntent)
            Log.d("AlarmUtils", "One-time alarm scheduled for $reminderId at ${Date(timeInMillis)}")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule one-time alarm", e)
        }
    }

    /**
     * Memeriksa apakah alarm masih aktif
     */
    fun isAlarmSet(context: Context, reminderId: String): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.medicineremindernew.ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent != null
    }

    /**
     * Helper function untuk mendapatkan data reminder dari Firestore
     * dan melakukan update alarm berdasarkan data terbaru
     */
    fun updateAlarmFromFirestore(context: Context, reminderId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("reminders").document(reminderId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tanggal = document.getString("tanggal") ?: ""
                    val waktu = document.getString("waktu") ?: ""
                    val recurrenceType = document.getString("recurrenceType") ?: "Sekali"

                    if (tanggal.isNotEmpty() && waktu.isNotEmpty()) {
                        // Parse dan schedule alarm baru berdasarkan data terbaru
                        try {
                            val dateTimeStr = "$tanggal $waktu"
                            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            val date = format.parse(dateTimeStr)

                            if (date != null) {
                                scheduleRecurringReminder(context, reminderId, date.time, recurrenceType)
                                Log.d("AlarmUtils", "Alarm refreshed from Firestore: $dateTimeStr")
                            }
                        } catch (e: Exception) {
                            Log.e("AlarmUtils", "Error parsing date/time from Firestore", e)
                        }
                    }
                } else {
                    Log.w("AlarmUtils", "Reminder document not found: $reminderId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AlarmUtils", "Error fetching reminder from Firestore", e)
            }
    }

    /**
     * Menjadwalkan alarm dengan aman, menangani permission dan fallback
     */
    private fun scheduleAlarmSafely(
        alarmManager: AlarmManager,
        timeInMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            // ✅ Android 12+ (API 31+) - Periksa permission exact alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    // Ada permission untuk exact alarm
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                    Log.d("AlarmUtils", "Exact alarm scheduled (API 31+)")
                } else {
                    // Fallback ke alarm biasa jika tidak ada permission
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                    Log.w("AlarmUtils", "Exact alarm permission not granted, using inexact alarm")
                }
            }
            // ✅ Android 6.0-11 (API 23-30)
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.d("AlarmUtils", "Exact alarm scheduled (API 23-30)")
            }
            // ✅ Android 4.4-5.x (API 19-22)
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.d("AlarmUtils", "Exact alarm scheduled (API 19-22)")
            }
            // ✅ Android < 4.4 (API < 19)
            else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.d("AlarmUtils", "Basic alarm scheduled (API < 19)")
            }
        } catch (securityException: SecurityException) {
            // ✅ Handle SecurityException untuk exact alarm
            Log.w("AlarmUtils", "SecurityException scheduling exact alarm, falling back to inexact", securityException)
            fallbackToInexactAlarm(alarmManager, timeInMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Unexpected error scheduling alarm", e)
            fallbackToInexactAlarm(alarmManager, timeInMillis, pendingIntent)
        }
    }
    /**
     * Fallback ke alarm yang tidak exact jika exact alarm gagal
     */
    private fun fallbackToInexactAlarm(
        alarmManager: AlarmManager,
        timeInMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
            Log.d("AlarmUtils", "Fallback inexact alarm scheduled")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule fallback alarm", e)
        }
    }

    /**
     * Periksa apakah aplikasi memiliki permission untuk exact alarm
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Versi Android lama tidak memerlukan permission khusus
        }
    }

    /**
     * Minta user untuk memberikan permission exact alarm
     */
    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                try {
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("AlarmUtils", "Failed to open exact alarm settings", e)
                }
            }
        }
    }
}