package com.example.medicineremindernew.ui.alarm

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

        // Ambil reminder ID dari Intent
        val reminderId = intent?.getStringExtra("reminderId") ?: "Unknown"

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
}
