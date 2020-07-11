package com.example.notasdobreno.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.Room.databaseBuilder
import com.example.notasdobreno.R
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.database.dao.NoteDao
import com.example.notasdobreno.model.Note
import com.example.notasdobreno.reminder.AlarmHandler
import com.example.notasdobreno.ui.activity.MainActivity
import com.example.notasdobreno.ui.activity.SplashScreenActivity
import java.util.*


class NotificationReceiver : BroadcastReceiver() {

    private lateinit var noteDao: NoteDao

    override fun onReceive(context: Context, intent: Intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Note notification"
            val descriptionText = "Note notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("AlarmId", name, importance)
            mChannel.description = descriptionText
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val noteId = intent.getLongExtra("noteId", 0L)
        val database = databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes-database")
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao()

        if (noteId > 0L) {
            val note = noteDao.findById(noteId)

            val notificationIntent = Intent(context, SplashScreenActivity::class.java)

            notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)

            // Create the notification to be shown
            val nBuilder = NotificationCompat.Builder(context, "AlarmId")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(note.title)
                .setContentText(note.resume)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)

            // Get the Notification manager service
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Generate an Id for each notification
            val id = System.currentTimeMillis() / 1000

            // Show a notification
            notificationManager.notify(id.toInt(), nBuilder.build())

            if (note.alertFrequency != Note.ONCE) {
                updateNoteAlert(context, note)
            }
        }
    }

    private fun updateNoteAlert(context: Context, note: Note) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = note.alertAt

        when (note.alertFrequency) {
            Note.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            Note.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            Note.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            Note.ANNUALLY -> calendar.add(Calendar.YEAR, 1)
        }

        note.alertAt = calendar.time
        noteDao.update(note)

        AlarmHandler.setAlarm(context, note)
    }
}