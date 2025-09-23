package com.example.medicineremindernew.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class AlarmPopupActivity : ComponentActivity() {
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmPopup", "onCreate: AlarmPopupActivity dimulai")

        // Menampilkan popup di atas layar kunci
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val reminderId = intent.getStringExtra("reminderId") ?:
                        intent.getStringExtra("reminder_id") ?: ""
        Log.d("AlarmPopup", "Reminder ID: $reminderId")

        // Putar suara alarm
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(this, alarmUri)
            ringtone?.play()
            Log.d("AlarmPopup", "Ringtone mulai diputar")
        } catch (e: Exception) {
            Log.e("AlarmPopup", "Gagal memutar ringtone: ${e.message}")
        }

        setContent {
            MedicineReminderNewTheme {
                AlarmPopupScreen(
                    reminderId = reminderId,
                    onDismiss = { lansiaList, obatList ->
                        Log.d("AlarmPopup", "Tombol 'Sudah Diminum' ditekan")

                        // 🔹 Update Firestore: statusIoT = "OFF"
                        matikanIoT(reminderId)

                        // 🔹 Simpan riwayat dengan jenis "minum obat"
                        simpanRiwayat(reminderId, lansiaList, obatList, jenisRiwayat = "minum obat")

                        // 🔹 Stop ringtone
                        if (ringtone?.isPlaying == true) {
                            ringtone?.stop()
                        }

                        // 🔹 Batalkan alarm
//                        cancelAlarm(this, reminderId)
                        finish()
                    },
                    onSnooze = {
                        Log.d("AlarmPopup", "Tombol 'Bunyikan 5 Menit Lagi' ditekan")

                        // 🔹 Hentikan ringtone saat ini
                        if (ringtone?.isPlaying == true) {
                            ringtone?.stop()
                            Log.d("AlarmPopup", "Ringtone dihentikan untuk snooze")
                        }

                        matikanIoT(reminderId)

                        // 🔹 Update Firestore: tambah 5 menit ke waktu reminder
//                        tambah5MenitReminder(reminderId)

                        // 🔹 Set alarm snooze
                        setSnoozeAlarm(this, reminderId)

                        finish()
                    }
                )
            }
        }

    }

    // 🔹 Fungsi update Firestore
    private fun matikanIoT(reminderId: String) {
        if (reminderId.isBlank()) {
            Log.e("Firestore", "Reminder ID tidak valid")
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("reminders").document(reminderId)
            .update("statusIoT", "OFF")
            .addOnSuccessListener {
                Log.d("Firestore", "statusIoT diupdate ke OFF untuk reminder $reminderId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal update statusIoT", e)
            }
    }

    private fun tambah5MenitReminder(reminderId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("reminders").document(reminderId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val reminder = doc.toObject(Reminder::class.java)
                    val tanggal = reminder?.tanggal  // "2025-08-27"
                    val waktu = reminder?.waktu      // "11:45"

                    if (tanggal != null && waktu != null) {
                        try {
                            // Gabungkan tanggal + waktu jadi satu string
                            val dateTimeStr = "$tanggal $waktu"  // "2025-08-27 11:45"
                            val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                            val date = format.parse(dateTimeStr)

                            // Tambah 5 menit
                            val calendar = Calendar.getInstance()
                            calendar.time = date!!
                            calendar.add(Calendar.MINUTE, 5)

                            // Pisahkan lagi jadi tanggal & waktu
                            val newTanggal = String.format(
                                "%04d-%02d-%02d",
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1,
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            val newWaktu = String.format(
                                "%02d:%02d",
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE)
                            )

                            // Update ke Firestore
                            db.collection("reminders").document(reminderId)
                                .update(
                                    mapOf(
                                        "tanggal" to newTanggal,
                                        "waktu" to newWaktu
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d("AlarmPopup", "Reminder diupdate: $newTanggal $newWaktu")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AlarmPopup", "Gagal update reminder: ${e.message}")
                                }
                        } catch (e: Exception) {
                            Log.e("AlarmPopup", "Error parsing waktu: ${e.message}")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AlarmPopup", "Gagal ambil reminder: ${e.message}")
            }
    }

    private fun setSnoozeAlarm(context: Context, reminderId: String) {
        try {
            // Hitung waktu 5 menit dari sekarang
            val snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("reminderId", reminderId)
                putExtra("reminder_id", reminderId) // Support backward compatibility
                putExtra("is_snooze", true)
                putExtra("is_recurring", false)
                action = "com.example.medicineremindernew.ALARM" // ✅ TAMBAHKAN ACTION
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode() + 1000, // ID berbeda untuk snooze
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // ✅ GUNAKAN METODE YANG SAMA SEPERTI DI AlarmUtils
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            snoozeTime,
                            pendingIntent
                        )
                        Log.d("AlarmPopup", "Exact snooze alarm scheduled for: ${java.util.Date(snoozeTime)}")
                    } else {
                        // Fallback ke inexact alarm
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            snoozeTime,
                            pendingIntent
                        )
                        Log.w("AlarmPopup", "Exact alarm permission not available, using inexact snooze alarm")
                    }
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        snoozeTime,
                        pendingIntent
                    )
                    Log.d("AlarmPopup", "Exact snooze alarm scheduled (API 23+)")
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        snoozeTime,
                        pendingIntent
                    )
                    Log.d("AlarmPopup", "Exact snooze alarm scheduled (API 19+)")
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        snoozeTime,
                        pendingIntent
                    )
                    Log.d("AlarmPopup", "Basic snooze alarm scheduled")
                }
            } catch (se: SecurityException) {
                Log.w("AlarmPopup", "SecurityException, fallback to inexact snooze alarm: ${se.message}")
                // Final fallback
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )
            }

        } catch (e: Exception) {
            Log.e("AlarmPopup", "Gagal mengatur snooze alarm: ${e.message}")
        }
    }

    private fun setRegularSnoozeAlarm(context: Context, reminderId: String, alarmManager: AlarmManager) {
        try {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("reminderId", reminderId)
                putExtra("is_snooze", true)
                putExtra("is_recurring", false)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode() + 1000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = System.currentTimeMillis() + (5 * 60 * 1000)

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )

            Log.d("AlarmPopup", "Regular snooze alarm diset untuk reminder $reminderId dalam 5 menit (non-exact)")
        } catch (e: Exception) {
            Log.e("AlarmPopup", "Gagal mengatur regular snooze alarm: ${e.message}")
        }
    }

    private fun simpanRiwayat(reminderId: String, lansiaList: List<Lansia>, obatList: List<Obat>,jenisRiwayat: String) {
        val db = FirebaseFirestore.getInstance()
        val riwayatId = db.collection("riwayat").document().id

        val riwayatData = hashMapOf(
            "idRiwayat" to riwayatId,
            "reminderId" to reminderId,
            "lansiaIds" to lansiaList.map { it.id }, // ✅ pakai id dari Lansia
            "obatIds" to obatList.map { it.id },     // ✅ pakai id dari Obat
            "waktuDiminum" to System.currentTimeMillis(),
            "status" to "SUDAH",
            "jenis" to jenisRiwayat
        )


        db.collection("riwayat")
            .document(riwayatId)
            .set(riwayatData)
            .addOnSuccessListener {
                Log.d("Riwayat", "Riwayat berhasil disimpan")
            }
            .addOnFailureListener { e ->
                Log.e("Riwayat", "Gagal menyimpan riwayat", e)
            }
    }

    override fun onDestroy() {
        try {
            ringtone?.stop()
            Log.d("AlarmPopup", "Ringtone dihentikan di onDestroy()")
        } catch (e: Exception) {
            Log.e("AlarmPopup", "Gagal menghentikan ringtone di onDestroy(): ${e.message}")
        }
        super.onDestroy()
    }
}

@Composable
fun AlarmPopupScreen(
    reminderId: String,
    onDismiss: (lansiaList: List<Lansia>, obatList: List<Obat>) -> Unit,
    onSnooze: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var reminder by remember { mutableStateOf<Reminder?>(null) }
    var obatList by remember { mutableStateOf<List<Obat>>(emptyList()) }
    var lansiaList by remember { mutableStateOf<List<Lansia>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(reminderId) {
        Log.d("AlarmPopup", "Memulai fetch reminder dengan ID: $reminderId")
        try {
            val reminderSnap = db.collection("reminders").document(reminderId).get().await()
            if (reminderSnap.exists()) {
                val fetchedReminder = reminderSnap.toObject(Reminder::class.java)
                reminder = fetchedReminder

                if (fetchedReminder != null) {
                    val obatTempList = mutableListOf<Obat>()
                    fetchedReminder.obatIds.forEach { obatId ->
                        val obatSnap = db.collection("obat").document(obatId).get().await()
                        obatSnap.toObject(Obat::class.java)?.let { obatTempList.add(it) }
                    }
                    obatList = obatTempList

                    val lansiaTempList = mutableListOf<Lansia>()
                    fetchedReminder.lansiaIds.forEach { lansiaId ->
                        val lansiaSnap = db.collection("lansia").document(lansiaId).get().await()
                        lansiaSnap.toObject(Lansia::class.java)?.let { lansiaTempList.add(it) }
                    }
                    lansiaList = lansiaTempList
                }
            } else {
                Log.e("AlarmPopup", "Reminder dengan ID $reminderId tidak ditemukan di Firestore")
            }
        } catch (e: Exception){
            Log.e("AlarmPopup", "Gagal mengambil data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.wrapContentSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(4.dp, Color(0xFF027A7E), RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Saatnya Minum Obat!", fontSize = 22.sp, color = Color(0xFF011A27))
            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> CircularProgressIndicator()
                reminder != null -> {
                    if (lansiaList.isNotEmpty()){
                        Text(
                            text = "Lansia:\n${lansiaList.joinToString(", "){ it.nama }}",
                            fontSize = 18.sp,
                            color = Color(0xFF011A27)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (obatList.isNotEmpty()){
                        obatList.forEach { obat ->
                            Text("Obat: ${obat.nama}", fontSize = 16.sp, color = Color(0xFF011A27))
                            Text("Dosis: ${obat.dosis}", fontSize = 14.sp, color = Color(0xFF011A27))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                else -> Text("Data reminder tidak tersedia", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onDismiss(lansiaList, obatList) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF027A7E),
                        contentColor = Color.White
                    )
                ) {
                    Text("Sudah Diminum", fontSize = 12.sp)
                }
                Button(
                    onClick = onSnooze,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    )
                ) {
                    Text("5 Menit Lagi", fontSize = 12.sp)
                }
            }
        }
    }
}
