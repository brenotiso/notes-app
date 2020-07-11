package com.example.notasdobreno.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.notasdobreno.*
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.database.dao.NoteDao
import com.example.notasdobreno.ui.activity.recyclerview.NotesListAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var noteDao: NoteDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "notes-database")
            .allowMainThreadQueries()
            .build()

        noteDao = database.noteDao()

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        adapter = NotesListAdapter(context = this)
        adapter.add(noteDao.all())

        recyclerView.adapter = adapter;
    }

    override fun onRestart() {
        super.onRestart()
        adapter.replaceAllNotes(noteDao.all())
    }

    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.newNote -> {
            startActivity(Intent(this, NewNoteActivity::class.java))
            true
        }
        R.id.leaveApp -> {
            // "minimiza o app"
            finish()

            // fecha o app
            //exitProcess(-1)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
