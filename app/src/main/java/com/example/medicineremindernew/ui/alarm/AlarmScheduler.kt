package com.example.medicineremindernew.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.example.medicineremindernew.ui.data.model.Reminder
import java.util.Calendar

fun canScheduleExactAlarm(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

fun scheduleAlarm(context: Context, reminder: Reminder) {
    // 🔐 Periksa apakah aplikasi diizinkan menjadwalkan exact alarm
    if (!canScheduleExactAlarm(context)) {
        Toast.makeText(context, "Aplikasi tidak diizinkan untuk menjadwalkan alarm tepat waktu", Toast.LENGTH_LONG).show()

        // Arahkan pengguna ke pengaturan untuk memberikan izin
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
        return
    }

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id, // ID unik agar bisa diupdate/cancel
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        time = reminder.tanggal
        val timeParts = reminder.waktu.toString().split(":")
        set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        set(Calendar.MINUTE, timeParts[1].toInt())
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val triggerTime = calendar.timeInMillis

    Log.d("ReminderDebug", "Menjadwalkan alarm pada ${reminder.waktu} tanggal ${reminder.tanggal}")
    Log.d("ReminderDebug", "Waktu millis: $triggerTime (Sekarang: ${System.currentTimeMillis()})")

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}
