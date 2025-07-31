package com.example.medicineremindernew.ui.alarm

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme

class AlarmPopupActivity : ComponentActivity() {
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menampilkan popup di atas layar kunci
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val reminderId = intent.getStringExtra("reminderId") ?: "Unknown"

        // Putar suara alarm
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, alarmUri)
        ringtone?.play()

        setContent {
            MedicineReminderNewTheme {
                AlarmPopupScreen(
                    onDismiss = {
                        ringtone?.stop()
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}

@Composable
fun AlarmPopupScreen(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF3E0)),
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
            Text("Obat: Paracetamol\nDosis: 2 tablet", fontSize = 16.sp, color = Color(0xFF011A27))
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
