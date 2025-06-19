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
        context?.let {
            val mediaPlayer = MediaPlayer.create(it, R.raw.nada1)
            mediaPlayer.start()

            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer.stop()
                mediaPlayer.release()
            }, 10_000)
        }
    }

}

