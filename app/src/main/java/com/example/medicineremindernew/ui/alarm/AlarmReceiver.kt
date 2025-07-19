package com.example.medicineremindernew.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm diterima, memulai AlarmService")

        context?.let {
            val serviceIntent = Intent(it, AlarmService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.startForegroundService(serviceIntent) // Android 8+
            } else {
                it.startService(serviceIntent) // Android 7 kebawah
            }
        }
    }


}
