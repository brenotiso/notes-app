package com.example.notasdobreno.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.reminder.AlarmHandler


class TimeChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action == "android.intent.action.TIME_SET") {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "notes-database"
            )
                .allowMainThreadQueries()
                .build()
            val noteDao = database.noteDao()
            var notes = noteDao.all()
            notes.forEach {
                AlarmHandler.setAlarm(context, it)
            }
        }
    }
}