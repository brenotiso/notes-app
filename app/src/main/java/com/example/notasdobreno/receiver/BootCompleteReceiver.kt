package com.example.notasdobreno.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room.databaseBuilder
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.reminder.AlarmHandler

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            val database = databaseBuilder(
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