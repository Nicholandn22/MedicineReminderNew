package com.example.medicineremindernew.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Ambil semua reminder dari database & jadwalkan ulang
            Log.d("BootReceiver", "Perangkat reboot, jadwalkan ulang alarm")
            // TODO: Ambil data reminder dari Firestore & panggil scheduleAlarm()
        }
    }
}
