package com.github.lauripiisang.freesleeptimer

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService


class CancelAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nm = getSystemService(context, NotificationManager::class.java) as NotificationManager
        nm.cancel(42)
        val alarmMgr = getSystemService(context, AlarmManager::class.java) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 4242, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (alarmIntent != null)
            alarmMgr.cancel(alarmIntent)
    }
}
