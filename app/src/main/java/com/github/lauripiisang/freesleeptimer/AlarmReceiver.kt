package com.github.lauripiisang.freesleeptimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import androidx.core.content.ContextCompat.getSystemService


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val am = getSystemService(context, AudioManager::class.java) as AudioManager
        am.requestAudioFocus(
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .build()
        )
    }
}
