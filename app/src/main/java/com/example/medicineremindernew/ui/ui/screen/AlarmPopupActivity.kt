package com.example.medicineremindernew.ui.alarm

import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
// import com.example.medicineremindernew.ui.alarm.cancelAlarm
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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

//        val reminderId = intent.getStringExtra("reminderId") ?: "Unknown"
        val reminderId = intent.getStringExtra("reminderId") ?: ""
        Log.d("AlarmPopup", "Reminder ID: $reminderId")

        // Putar suara alarm
//        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        ringtone = RingtoneManager.getRingtone(this, alarmUri)
//        ringtone?.play()

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
                    onDismiss = {
//                        ringtone?.stop()
//                        cancelAlarm(this, reminderId) // <-- Mematikan alarm dari AlarmManager
//                        finish()
                        Log.d("AlarmPopup", "Tombol 'Matikan Alarm' ditekan")

                        // 🔹 Update Firestore: statusIoT = "OFF"
                        matikanIoT(reminderId)

                        if (ringtone != null) {
                            if (ringtone!!.isPlaying) {
                                Log.d("AlarmPopup", "Ringtone sedang berbunyi, akan dihentikan.")
                                ringtone?.stop()
                            } else {
                                Log.d("AlarmPopup", "Ringtone tidak sedang berbunyi.")
                            }
                        } else {
                            Log.d("AlarmPopup", "Ringtone null")
                        }

                        cancelAlarm(this, reminderId)
                        Log.d("AlarmPopup", "AlarmManager dibatalkan")
                        finish()
                    }
                )
            }
        }
    }

    // 🔹 Fungsi update Firestore
    private fun matikanIoT(reminderId: String) {
        if (reminderId == "Unknown") {
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

    override fun onDestroy() {
//        ringtone?.stop()
//        super.onDestroy()
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
    onDismiss: () -> Unit
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
            Log.d("AlarmPopup", "Snapshot reminder berhasil diambil: ${reminderSnap.exists()}")

            if (reminderSnap.exists()) {
                val fetchedReminder = reminderSnap.toObject(Reminder::class.java)
                reminder = fetchedReminder

                if (fetchedReminder != null) {
                    // Fetch multiple obat berdasarkan obatIds
                    val obatTempList = mutableListOf<Obat>()
                    fetchedReminder.obatIds.forEach { obatId ->
                        try {
                            val obatSnap = db.collection("obat").document(obatId).get().await()
                            obatSnap.toObject(Obat::class.java)?.let { obatTempList.add(it) }
                        } catch (e: Exception) {
                            Log.e("AlarmPopup", "Gagal mengambil obat dengan ID $obatId: ${e.message}")
                        }
                    }
                    obatList = obatTempList

                    val lansiaTempList = mutableListOf<Lansia>()
                    fetchedReminder.lansiaIds.forEach { lansiaId ->
                        try {
                            val lansiaSnap = db.collection("lansia").document(lansiaId).get().await()
                            lansiaSnap.toObject(Lansia::class.java)?.let { lansiaTempList.add(it) }
                        } catch (e: Exception) {
                            Log.e("AlarmPopup", "Gagal mengambil lansia dengan ID $lansiaId: ${e.message}")
                        }
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
        modifier = Modifier
//            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp),
//            .background(Color(0xFFFAF3E0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 4.dp,
                    color = Color(0xFF027A7E),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Saatnya Minum Obat!", fontSize = 22.sp, color = Color(0xFF011A27))
            Spacer(modifier = Modifier.height(12.dp))
//            Text("Obat: Paracetamol\nDosis: 2 tablet", fontSize = 16.sp, color = Color(0xFF011A27))
//            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                reminder != null && obatList.isNotEmpty() && lansiaList.isNotEmpty() -> {
                    Text(
                        text = "Pasien: ${lansiaList.joinToString(", ") { it.nama }}",
                        fontSize = 16.sp,
                        color = Color(0xFF011A27)
                    )
                }
                else -> {
                    Text("Data reminder tidak tersedia", color = Color.Red)
                }
            }

//            if (reminder != null && obat != null){
////                Text(
////                    text = "Obat: ${obat?.nama}\nDosis: ${obat?.dosis}",
////                    fontSize = 16.sp,
////                    color = Color(0xFF011A27)
////                )
//                Text("Data reminder tidak tersedia", color = Color.Red)
//            } else {
//                CircularProgressIndicator()
//            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF027A7E),
                    contentColor = Color.White
                )
            ) {
                Text("Matikan Alarm")
            }
        }
    }
}
