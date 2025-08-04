package com.example.medicineremindernew.ui.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medicineremindernew.R
import android.app.AlarmManager
import android.app.PendingIntent


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm diterima!")
        createNotificationChannel(context)

        val reminderId = intent?.getStringExtra("reminderId") ?: "Unknown"

//        try {
//            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
//            ringtone.play()
//        } catch (e: Exception) {
//            Log.e("AlarmReceiver", "Gagal memutar alarm: ${e.message}")
//        }

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.pill)
            .setContentTitle("Pengingat Obat")
            .setContentText("Saatnya minum obat (ID: $reminderId)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(reminderId.hashCode(), notification)

        val popupIntent = Intent(context, AlarmPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("reminderId", reminderId)
        }
        context.startActivity(popupIntent)
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




}
