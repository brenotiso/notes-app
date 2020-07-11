package com.example.notasdobreno.ui.activity.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.notasdobreno.R
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.database.dao.NoteDao
import com.example.notasdobreno.model.Note
import com.example.notasdobreno.reminder.AlarmHandler
import com.example.notasdobreno.ui.activity.ShowNoteActivity
import kotlinx.android.synthetic.main.adapter_note_card.view.*
import java.text.SimpleDateFormat


class NotesListAdapter(
    private val notes: MutableList<Note> = mutableListOf(),
    private val context: Context
) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    private var noteDao: NoteDao

    init {
        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes-database")
            .allowMainThreadQueries()
            .build()

        noteDao = database.noteDao()
    }

    override fun onCreateViewHolder(vg: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(vg.context).inflate(R.layout.adapter_note_card, vg, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]

        holder.bind(context, note, fun () {
            noteDao.delete(note)
            AlarmHandler.cancelAlarm(this.context, note)
            this.notes.remove(note)
            notifyDataSetChanged()
        })
    }

    fun add(notes: List<Note>) {
        this.notes.addAll(notes)
        notifyItemRangeInserted(0, notes.size)
    }

    fun replaceAllNotes(notes: List<Note>) {
        this.notes.clear()
        this.notes.addAll(notes)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id: TextView = itemView.noteId
        private val title: TextView = itemView.noteTitle
        private val resume: TextView = itemView.resume
        private val createAt: TextView = itemView.create_at
        private val alertAt: TextView = itemView.alert_at
        private val buttonDelete: ImageButton = itemView.buttonDelete
        private val card: CardView = itemView.card

        fun bind(context: Context, note: Note, deleteNote: () -> Unit) {
            id.text = "#" + note.id
            title.text = note.title
            resume.text = note.resume
            createAt.text = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(note.createAt)
            alertAt.text = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(note.alertAt)

            buttonDelete.setOnClickListener { buttonDeleteListener(context, note.id, deleteNote) }

            card.setOnClickListener { cardListener(context, note.id) }
        }

        private fun cardListener(context: Context, noteId: Long) {
            val intent = Intent(context, ShowNoteActivity::class.java)
            intent.putExtra("noteId", noteId)
            context.startActivity(intent)
        }

        private fun buttonDeleteListener(context: Context, noteId: Long, deleteNote: () -> Unit) {
            val builder = AlertDialog.Builder(context)

            builder.setTitle(context.getString(R.string.dialog_title_delete_note))

            builder.setMessage(context.getString(R.string.dialog_msg_delete_note, noteId))

            builder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                // deletar a nota
                deleteNote()

                Toast.makeText(context, context.getString(R.string.msg_delete_note), Toast.LENGTH_SHORT).show()
            }

            // faz nada
            builder.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }

            val dialog: AlertDialog = builder.create()

            dialog.show()
        }
    }
}