package com.github.lauripiisang.freesleeptimer

import android.app.*
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.EditText
import java.util.*
import android.widget.Toast
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {
    val CHANNEL_ID = "FST-notifications-42"
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "FreeSleepTimer",
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        val setTimer: Button = findViewById(R.id.set_timer)
        val timerMins: EditText = findViewById(R.id.timer_mins)
        setTimer.setOnClickListener(setSleepTimer(timerMins))

        val addTimer: Button = findViewById(R.id.timer_add)
        val subTimer: Button = findViewById(R.id.timer_sub)

        addTimer.setOnClickListener {
            timerMins.setText((Integer.valueOf(timerMins.text.toString()) + 5).toString())
        }

        subTimer.setOnClickListener {
            val subtracted = (Integer.valueOf(timerMins.text.toString()) - 5)
            if (subtracted < 1) {
                timerMins.setText(1.toString())
            } else {
                timerMins.setText(subtracted.toString())
            }

        }
    }

    private fun setSleepTimer(
        timerMins: EditText
    ): (v: View) -> Unit {
        return {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(42)
            val stringMinutes = timerMins.text.toString()
            val inputTimerMins = Integer.valueOf(stringMinutes)
            //get the current timeStamp
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, inputTimerMins)
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss a")
            val strDate = simpleDateFormat.format(calendar.time)

            //show the toast
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, "Pausing sound at $strDate", duration)
            toast.show()

            val cancelIntent = Intent(it.context, CancelAlarmReceiver::class.java).apply {
                action = "CANCEL"
                putExtra(EXTRA_NOTIFICATION_ID, 42)
            }
            val cancelPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(it.context, 555, cancelIntent, 0)

            val timeOut = inputTimerMins * 60 * 1000L
            val notification = createNotification(it, strDate, cancelPendingIntent, timeOut)

            alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmIntent = Intent(it.context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    it.context,
                    4242,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            alarmMgr?.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
            nm.notify(42, notification.build())
        }
    }

    private fun createNotification(
        it: View,
        strDate: String,
        cancelPendingIntent: PendingIntent,
        timeOut: Long
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(it.context, CHANNEL_ID)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("Sleep Timer")
            .setContentText("Pausing sound at $strDate")
            .addAction(R.drawable.ic_cancel_black_24dp, "CANCEL", cancelPendingIntent)
            .setOngoing(true)
            .setTimeoutAfter(timeOut)
    }
}
