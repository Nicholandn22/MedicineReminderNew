package com.example.medicineremindernew.ui.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medicineremindernew.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm diterima!")
        createNotificationChannel(context)

        // ✅ TAMBAHAN: Ambil data untuk recurring alarm
        val reminderId = intent?.getStringExtra("reminderId") ?:
        intent?.getStringExtra("reminder_id") ?: "Unknown" // Support kedua format

        val recurrenceType = intent?.getStringExtra("recurrence_type")
        val intervalMillis = intent?.getLongExtra("interval_millis", 0L) ?: 0L
        val isRecurring = intent?.getBooleanExtra("is_recurring", false) ?: false

        Log.d("AlarmReceiver", "Reminder ID: $reminderId, Recurring: $isRecurring, Type: $recurrenceType")

        // 🔔 Tampilkan notifikasi
        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.pill)
            .setContentTitle("Pengingat Obat")
            .setContentText("Saatnya minum obat (ID: $reminderId)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(reminderId.hashCode(), notification)

        // 🧠 Buka popup activity
        val popupIntent = Intent(context, AlarmPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("reminderId", reminderId)
        }


        context.startActivity(popupIntent)

        // ✅ Trigger Firestore untuk ESP8266
        triggerActiveAlarm(reminderId)

        // ✅ TAMBAHAN: Jadwalkan alarm berikutnya jika ini recurring alarm
        if (isRecurring && intervalMillis > 0 && !recurrenceType.isNullOrEmpty()) {
            scheduleNextRecurringAlarm(context, reminderId, intervalMillis, recurrenceType)
        }
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

    private fun triggerActiveAlarm(reminderId: String) {
        try {
            val db = FirebaseFirestore.getInstance()

            val activeAlarmData = hashMapOf(
                "reminderId" to reminderId,
                "trigger" to true,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("active_alarm")
                .document("current")
                .set(activeAlarmData)
                .addOnSuccessListener {
                    Log.d("AlarmReceiver", "active_alarm triggered for reminderId: $reminderId")
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmReceiver", "Gagal menulis active_alarm: ${e.message}")
                }

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Exception saat trigger active_alarm: ${e.message}")
        }
    }

    // ✅ FUNGSI BARU: Jadwalkan alarm berikutnya untuk recurring
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNextRecurringAlarm(
        context: Context,
        reminderId: String,
        intervalMillis: Long,
        recurrenceType: String
    ) {
        try {
            val nextTimeInMillis = System.currentTimeMillis() + intervalMillis

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

            val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("reminderId", reminderId) // Gunakan format yang sama dengan kode Anda
                putExtra("reminder_id", reminderId) // Backup untuk kompatibilitas
                putExtra("recurrence_type", recurrenceType)
                putExtra("interval_millis", intervalMillis)
                putExtra("is_recurring", true)
            }

            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                nextIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            // ✅ Jadwalkan alarm exact berikutnya
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    nextTimeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    android.app.AlarmManager.RTC_WAKEUP,
                    nextTimeInMillis,
                    pendingIntent
                )
            }

            Log.d("AlarmReceiver", "Next recurring alarm scheduled for: ${java.util.Date(nextTimeInMillis)}")
            Log.d("AlarmReceiver", "Recurrence type: $recurrenceType, Interval: ${intervalMillis / (1000 * 60 * 60)} hours")

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Failed to schedule next recurring alarm", e)
        }
    }
}