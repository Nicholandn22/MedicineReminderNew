package com.example.medicineremindernew.ui.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
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
            // ✅ Periksa dan jadwalkan alarm berdasarkan kemampuan device
            scheduleAlarmSafely(alarmManager, timeInMillis, pendingIntent)

            Log.d("AlarmUtils", "Recurring alarm scheduled for $reminderId with type: $recurrenceType at ${Date(timeInMillis)}")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule recurring alarm", e)
        }
    }

    /**
     * Menghitung waktu alarm berikutnya berdasarkan jenis recurrence
     */
    fun calculateNextAlarmTime(originalTimeInMillis: Long, recurrenceType: String): Long {
        val calendar = Calendar.getInstance()
        val originalCalendar = Calendar.getInstance().apply {
            timeInMillis = originalTimeInMillis
        }

        return when (recurrenceType) {
            "Harian" -> {
                // Untuk harian: ambil jam dan menit dari alarm original
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, originalCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, originalCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // Jika waktu sudah terlewat hari ini, jadwalkan untuk besok
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }.timeInMillis
            }

            "Mingguan" -> {
                // Untuk mingguan: ambil hari, jam, dan menit dari alarm original
                calendar.apply {
                    set(Calendar.DAY_OF_WEEK, originalCalendar.get(Calendar.DAY_OF_WEEK))
                    set(Calendar.HOUR_OF_DAY, originalCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, originalCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // Jika waktu sudah terlewat minggu ini, jadwalkan untuk minggu depan
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                }.timeInMillis
            }

            "Bulanan" -> {
                // Untuk bulanan: ambil tanggal, jam, dan menit dari alarm original
                calendar.apply {
                    val originalDay = originalCalendar.get(Calendar.DAY_OF_MONTH)
                    set(Calendar.DAY_OF_MONTH, 1) // Set ke tanggal 1 dulu
                    set(Calendar.HOUR_OF_DAY, originalCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, originalCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // Pastikan tanggal valid untuk bulan ini
                    val maxDayInMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
                    val targetDay = if (originalDay <= maxDayInMonth) originalDay else maxDayInMonth
                    set(Calendar.DAY_OF_MONTH, targetDay)

                    // Jika waktu sudah terlewat bulan ini, jadwalkan untuk bulan depan
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.MONTH, 1)
                        // Periksa lagi untuk bulan berikutnya
                        val nextMaxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                        val nextTargetDay = if (originalDay <= nextMaxDay) originalDay else nextMaxDay
                        set(Calendar.DAY_OF_MONTH, nextTargetDay)
                    }
                }.timeInMillis
            }

            else -> {
                // Default ke harian
                calculateNextAlarmTime(originalTimeInMillis, "Harian")
            }
        }
    }

    /**
     * Menjadwalkan alarm berikutnya setelah alarm saat ini berbunyi
     * Dipanggil dari AlarmReceiver
     */
    fun scheduleNextRecurringAlarm(
        context: Context,
        reminderId: String,
        originalTimeInMillis: Long,
        recurrenceType: String
    ) {
        val nextTimeInMillis = calculateNextAlarmTime(originalTimeInMillis, recurrenceType)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("recurrence_type", recurrenceType)
            putExtra("original_time", originalTimeInMillis)
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
            Log.d("AlarmUtils", "Next recurring alarm scheduled for $reminderId at ${Date(nextTimeInMillis)}")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule next recurring alarm", e)
        }
    }


    /**
     * Menjadwalkan alarm berikutnya setelah alarm saat ini berbunyi
     */
//    fun scheduleNextAlarm(
//        context: Context,
//        reminderId: String,
//        intervalMillis: Long,
//        recurrenceType: String
//    ) {
//        val nextTimeInMillis = System.currentTimeMillis() + intervalMillis
//        scheduleNextExactAlarm(context, reminderId, nextTimeInMillis, intervalMillis, recurrenceType)
//
//        Log.d("AlarmUtils", "Next alarm scheduled for: ${Date(nextTimeInMillis)}")
//    }

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