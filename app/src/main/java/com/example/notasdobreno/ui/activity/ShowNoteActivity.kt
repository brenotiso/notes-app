package com.example.notasdobreno.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.notasdobreno.R
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.database.dao.NoteDao
import com.example.notasdobreno.model.Note

import kotlinx.android.synthetic.main.activity_show_note.*
import kotlinx.android.synthetic.main.activity_show_note.toolbar
import kotlinx.android.synthetic.main.content_show_note.*
import kotlinx.android.synthetic.main.content_show_note.textFrequency
import java.text.SimpleDateFormat

class ShowNoteActivity() : AppCompatActivity() {

    private lateinit var noteDao: NoteDao
    private var noteId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_note)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "notes-database")
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao()

        noteId = intent.getLongExtra("noteId", 0L)

        setNote()

        fab.setOnClickListener { _ ->
            val intent = Intent(this, NewNoteActivity::class.java)
            intent.putExtra("noteId", this.noteId)
            startActivity(intent)
        }
    }

    private fun setNote() {
        val note = noteDao.findById(this.noteId)

        textId.text = note.id.toString()
        textTitle.text = note.title
        textResume.text = note.resume
        textDescription.text = note.description
        textCreateAt.text = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(note.createAt)
        textAlertAt.text = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(note.alertAt)
        textFrequency.text = frequencyToString(note.alertFrequency)
    }

    private fun frequencyToString(freq: Int): String {
        return when (freq) {
            Note.ONCE -> this.getString(R.string.once)
            Note.DAILY -> this.getString(R.string.daily)
            Note.WEEKLY -> this.getString(R.string.weekly)
            Note.MONTHLY -> this.getString(R.string.monthly)
            Note.ANNUALLY -> this.getString(R.string.annually)
            else -> this.getString(R.string.once)
        }
    }

    override fun onRestart() {
        super.onRestart()
        setNote()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
