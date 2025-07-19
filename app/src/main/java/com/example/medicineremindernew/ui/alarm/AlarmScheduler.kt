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
import java.text.SimpleDateFormat
import java.util.*

fun canScheduleExactAlarm(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else true
}

fun scheduleAlarm(context: Context, reminder: Reminder) {
    if (!canScheduleExactAlarm(context)) {
        Toast.makeText(context, "Aplikasi tidak diizinkan menjadwalkan alarm tepat waktu", Toast.LENGTH_LONG).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
        return
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("reminderId", reminder.id)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(reminder.tanggal)

    val calendar = Calendar.getInstance().apply {
        time = date ?: Date()
        val timeParts = reminder.waktu.split(":")
        set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        set(Calendar.MINUTE, timeParts[1].toInt())
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val triggerTime = calendar.timeInMillis
    if (triggerTime <= System.currentTimeMillis()) {
        Log.e("AlarmScheduler", "Waktu sudah lewat! Alarm tidak diatur.")
        return
    }

    Log.d("AlarmScheduler", "Menjadwalkan alarm ID=${reminder.id} pada ${reminder.tanggal} ${reminder.waktu}")

    if (reminder.pengulangan.lowercase() == "harian") {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    } else {
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
            pendingIntent
        )


    }
}
