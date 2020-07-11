package com.example.notasdobreno.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.notasdobreno.model.Note
import com.example.notasdobreno.receiver.NotificationReceiver


object  AlarmHandler {

    fun setAlarm(context: Context, note: Note) {

        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, NotificationReceiver::class.java)
        broadcastIntent.putExtra("noteId", note.id)

        val pIntent = PendingIntent.getBroadcast(
            context,
            0,
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Setting up AlarmManager
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (System.currentTimeMillis() < note.alertAt.time) {
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                note.alertAt.time,
                pIntent
            )
        }
    }

    fun cancelAlarm(context: Context, note: Note) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val broadcastIntent = Intent(context, NotificationReceiver::class.java)
        broadcastIntent.putExtra("noteId", note.id)
        val pIntent = PendingIntent.getBroadcast(
            context,
            0,
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmMgr.cancel(pIntent)
    }
}