package com.example.medicineremindernew.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.medicineremindernew.R


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.nada1) // pastikan ada di res/raw
        mediaPlayer.start()

        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.stop()
            mediaPlayer.release()
        }, 10_000)
    }
}

