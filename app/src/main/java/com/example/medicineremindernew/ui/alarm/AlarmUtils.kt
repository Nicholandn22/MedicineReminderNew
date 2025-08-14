package com.example.medicineremindernew.ui.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMillis = when (recurrenceType) {
            "Harian" -> AlarmManager.INTERVAL_DAY
            "Mingguan" -> AlarmManager.INTERVAL_DAY * 7
            "Bulanan" -> AlarmManager.INTERVAL_DAY * 30
            else -> AlarmManager.INTERVAL_DAY // Default harian
        }

        try {
            // ✅ Untuk Android 6.0+ gunakan setExactAndAllowWhileIdle untuk akurasi tinggi
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scheduleNextExactAlarm(context, reminderId, timeInMillis, intervalMillis, recurrenceType)
            } else {
                // ✅ Untuk versi Android lama, gunakan setRepeating
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    intervalMillis,
                    pendingIntent
                )
            }

            Log.d("AlarmUtils", "Recurring alarm scheduled for $reminderId with type: $recurrenceType")
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Failed to schedule recurring alarm", e)
        }
    }

    /**
     * Menjadwalkan alarm exact berikutnya (untuk Android 6.0+)
     * Ini diperlukan karena setRepeating tidak akurat pada Doze mode
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNextExactAlarm(
        context: Context,
        reminderId: String,
        timeInMillis: Long,
        intervalMillis: Long,
        recurrenceType: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("recurrence_type", recurrenceType)
            putExtra("interval_millis", intervalMillis)
            putExtra("is_recurring", true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ Jadwalkan alarm exact pertama
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }

        Log.d("AlarmUtils", "Exact alarm scheduled for: ${Date(timeInMillis)}")
    }

    /**
     * Menjadwalkan alarm berikutnya setelah alarm saat ini berbunyi
     */
    fun scheduleNextAlarm(
        context: Context,
        reminderId: String,
        intervalMillis: Long,
        recurrenceType: String
    ) {
        val nextTimeInMillis = System.currentTimeMillis() + intervalMillis
        scheduleNextExactAlarm(context, reminderId, nextTimeInMillis, intervalMillis, recurrenceType)

        Log.d("AlarmUtils", "Next alarm scheduled for: ${Date(nextTimeInMillis)}")
    }

    /**
     * Membatalkan alarm berulang
     */
    fun cancelRecurringAlarm(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d("AlarmUtils", "Recurring alarm cancelled for: $reminderId")
    }
}