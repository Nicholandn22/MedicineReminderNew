package com.example.medicineremindernew.ui.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.medicineremindernew.R
import com.example.medicineremindernew.ui.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm diterima!")

        // ✅ Periksa action untuk memastikan ini adalah alarm yang benar
        if (intent?.action != "com.example.medicineremindernew.ALARM") {
            Log.d("AlarmReceiver", "Received intent with wrong action: ${intent?.action}")
            return
        }

        createNotificationChannel(context)

        // ✅ Ambil data untuk recurring alarm - support kedua format untuk backward compatibility
        val reminderId = intent.getStringExtra("reminderId") ?:
        intent.getStringExtra("reminder_id") ?: "Unknown"

        val recurrenceType = intent.getStringExtra("recurrence_type")
        val originalTime = intent.getLongExtra("original_time", 0L)
        val intervalMillis = intent.getLongExtra("interval_millis", 0L)
        val isRecurring = intent.getBooleanExtra("is_recurring", false)
        val isSnooze = intent.getBooleanExtra("is_snooze", false)

        Log.d("AlarmReceiver", "Reminder ID: $reminderId, Recurring: $isRecurring, Type: $recurrenceType, Snooze: $isSnooze")

        // ✅ Set sebagai alarm aktif SEBELUM menampilkan popup
        AlarmPopupActivity.setActiveReminder(context, reminderId)

        // ✅ PERBAIKAN UTAMA: Putar suara alarm SEBELUM popup/notifikasi
        if (!isSnooze) {
            // Hanya putar suara untuk alarm baru, tidak untuk snooze
            AlarmPopupActivity.playGlobalRingtone(context)
        } else {
            // Untuk snooze, tetap putar tapi dengan volume yang sama
            AlarmPopupActivity.playGlobalRingtone(context)
        }

        // 🔔 Tampilkan notifikasi
        showNotification(context, reminderId, recurrenceType ?: "Sekali", isSnooze)

        // 🧠 Buka popup activity
        val popupIntent = Intent(context, AlarmPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("reminderId", reminderId)
            putExtra("reminder_id", reminderId) // Backward compatibility
        }

        try {
            context.startActivity(popupIntent)
            Log.d("AlarmReceiver", "AlarmPopupActivity started successfully")
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Failed to start AlarmPopupActivity: ${e.message}")

            // Fallback: show notification if popup fails
            showFallbackNotification(context, reminderId, isSnooze)
        }

        // ✅ Trigger Firestore untuk ESP8266
        triggerActiveAlarm(reminderId, isSnooze)

        // Jadwalkan alarm berikutnya jika ini recurring alarm
        if (isRecurring && !recurrenceType.isNullOrEmpty() && !isSnooze) {
            if (originalTime > 0) {
                AlarmUtils.scheduleNextRecurringAlarm(
                    context,
                    reminderId,
                    originalTime,
                    recurrenceType
                )
            } else if (intervalMillis > 0) {
                // Fallback ke metode lama jika originalTime tidak ada
                scheduleNextRecurringAlarmLegacy(context, reminderId, intervalMillis, recurrenceType)
            }
        }

        // ✅ Play alarm sound hanya jika bukan snooze atau tidak ada alarm lain yang sedang berbunyi
//        if (!isSnooze) {
//            playAlarmSound(context)
//        }
        // Opsional: Tambahkan suara alarm atau vibration
//        playAlarmSound(context)
    }

    private fun showNotification(context: Context, reminderId: String, recurrenceType: String, isSnooze: Boolean = false) {
        // Tentukan title dan text berdasarkan status snooze
        val notificationTitle = if (isSnooze) "Pengingat Obat (Snooze)" else "Pengingat Obat"
        val notificationText = if (isSnooze)
            "Waktunya minum obat! (Pengingat Snooze - ID: $reminderId)"
        else
            "Saatnya minum obat - $recurrenceType (ID: $reminderId)"

        // Intent untuk membuka aplikasi ketika notifikasi diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminderId", reminderId)
            putExtra("check_active_alarm", true) // ✅ Flag untuk check alarm aktif
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent untuk menandai sebagai sudah diminum
        val takenIntent = Intent(context, MedicineTakenReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.pill) // Sesuaikan dengan icon yang Anda punya
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE) // ✅ Hanya vibration, bukan sound
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.pill, // Ganti dengan icon check jika ada
                "Sudah Diminum",
                takenPendingIntent
            )
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setFullScreenIntent(pendingIntent, true) // ✅ Tambahan untuk fullscreen popup
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Gunakan ID yang berbeda untuk snooze notifications agar tidak menimpa notifikasi asli
        val notificationId = if (isSnooze) {
            (reminderId.hashCode() + 1000)
        } else {
            reminderId.hashCode()
        }

        try {
            manager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Log.e("AlarmReceiver", "Permission denied for notification", e)
        }
    }

    // ✅ Fallback notification jika popup gagal dibuka
    private fun showFallbackNotification(context: Context, reminderId: String, isSnooze: Boolean) {
        val notificationTitle = "Pengingat Obat - Buka Aplikasi"
        val notificationText = "Ada alarm aktif. Buka aplikasi untuk melihat pengingat."

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminderId", reminderId)
            putExtra("check_active_alarm", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.hashCode() + 2000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.pill)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(false) // Don't auto-cancel
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = reminderId.hashCode() + 2000

        try {
            manager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Log.e("AlarmReceiver", "Permission denied for fallback notification", e)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk alarm pengingat obat"
                enableVibration(true)
//                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun triggerActiveAlarm(reminderId: String, isSnooze: Boolean = false) {
        try {
            val db = FirebaseFirestore.getInstance()

            val activeAlarmData = hashMapOf(
                "reminderId" to reminderId,
                "trigger" to true,
                "isSnooze" to isSnooze,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("active_alarm")
                .document("current")
                .set(activeAlarmData)
                .addOnSuccessListener {
                    val alarmType = if (isSnooze) "snooze" else "regular"
                    Log.d("AlarmReceiver", "active_alarm triggered for reminderId: $reminderId (type: $alarmType)")
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmReceiver", "Gagal menulis active_alarm: ${e.message}")
                }

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Exception saat trigger active_alarm: ${e.message}")
        }
    }

    private fun playAlarmSound(context: Context) {
        try {
            // ✅ Hentikan ringtone yang mungkin masih berjalan sebelum memutar yang baru
//            AlarmPopupActivity.stopCurrentRingtone()

            val ringtoneManager = RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            )

            // ✅ Hanya putar jika belum ada yang sedang berbunyi
            if (ringtoneManager != null && !ringtoneManager.isPlaying) {
                ringtoneManager.play()
                Log.d("AlarmReceiver", "Alarm sound started")

                // ✅ TAMBAHAN: Hentikan suara otomatis setelah 30 detik sebagai safety
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try {
                        if (ringtoneManager.isPlaying) {
                            ringtoneManager.stop()
                            Log.d("AlarmReceiver", "Alarm sound stopped automatically after 30 seconds")
                        }
                    } catch (e: Exception) {
                        Log.e("AlarmReceiver", "Error stopping alarm sound automatically: ${e.message}")
                    }
                }, 30000) // 30 detik
            } else {
                Log.d("AlarmReceiver", "Ringtone is null or already playing, skipping")
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Failed to play alarm sound", e)
        }
    }

    // ✅ Fallback method untuk backward compatibility dengan sistem lama
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNextRecurringAlarmLegacy(
        context: Context,
        reminderId: String,
        intervalMillis: Long,
        recurrenceType: String
    ) {
        try {
            val nextTimeInMillis = System.currentTimeMillis() + intervalMillis
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

            val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("reminderId", reminderId)
                putExtra("reminder_id", reminderId)
                putExtra("recurrence_type", recurrenceType)
                putExtra("interval_millis", intervalMillis)
                putExtra("is_recurring", true)
                putExtra("is_snooze", false)
                action = "com.example.medicineremindernew.ALARM"
            }

            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                nextIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            // ✅ Gunakan fungsi aman dari AlarmUtils
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
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
                        Log.d("AlarmReceiver", "Next exact recurring alarm scheduled for: ${java.util.Date(nextTimeInMillis)}")
                    } else {
                        // Fallback ke alarm biasa
                        alarmManager.setAndAllowWhileIdle(
                            android.app.AlarmManager.RTC_WAKEUP,
                            nextTimeInMillis,
                            pendingIntent
                        )
                        Log.w("AlarmReceiver", "Exact alarm permission not available, using inexact alarm")
                    }
                } else {
                    // Android < 12
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
                    Log.d("AlarmReceiver", "Next exact recurring alarm scheduled for: ${java.util.Date(nextTimeInMillis)}")
                }
            } catch (se: SecurityException) {
                Log.w("AlarmReceiver", "SecurityException saat set exact recurring alarm, fallback ke regular alarm: ${se.message}")
                // Fallback final ke alarm biasa
                alarmManager.set(
                    android.app.AlarmManager.RTC_WAKEUP,
                    nextTimeInMillis,
                    pendingIntent
                )
                Log.d("AlarmReceiver", "Fallback regular recurring alarm scheduled")
            }

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Failed to schedule next recurring alarm", e)
        }
    }
}

// Receiver untuk menangani aksi "Sudah Diminum"
class MedicineTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminderId") ?:
        intent.getStringExtra("reminder_id") ?: return

        // Batalkan notifikasi
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(reminderId.hashCode())
        notificationManager.cancel(reminderId.hashCode() + 1000) // Cancel snooze notification too

        Log.d("MedicineTakenReceiver", "Medicine taken for reminder: $reminderId")

        // ✅ Clear active reminder
        AlarmPopupActivity.clearActiveReminder(context)

        // ✅ Update Firestore bahwa obat sudah diminum
        updateMedicineTakenStatus(reminderId)
    }

    private fun updateMedicineTakenStatus(reminderId: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val medicineStatus = hashMapOf(
                "reminderId" to reminderId,
                "status" to "taken",
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("medicine_status")
                .document(reminderId)
                .set(medicineStatus)
                .addOnSuccessListener {
                    Log.d("MedicineTakenReceiver", "Medicine status updated for: $reminderId")
                }
                .addOnFailureListener { e ->
                    Log.e("MedicineTakenReceiver", "Failed to update medicine status: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("MedicineTakenReceiver", "Exception updating medicine status: ${e.message}")
        }
    }
}