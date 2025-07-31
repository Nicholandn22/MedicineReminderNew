package com.example.medicineremindernew.ui.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
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

fun scheduleAlarm(context: Context, reminderId: String, timeInMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        }
    }

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = "com.example.medicineremindernew.ALARM"
        putExtra("reminderId", reminderId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeInMillis,
        pendingIntent
    )

    Log.d("AlarmScheduler", "Menjadwalkan alarm $reminderId pada ${Date(timeInMillis)}")
}


fun cancelAlarm(context: Context, reminderId: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = "com.example.medicineremindernew.ALARM"
        putExtra("reminderId", reminderId) // ⬅️ tambahkan ini!
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)

    Log.d("AlarmScheduler", "Alarm dengan ID $reminderId berhasil dibatalkan.")
}







private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "alarm_channel",
            "Alarm Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel untuk alarm pengingat"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

