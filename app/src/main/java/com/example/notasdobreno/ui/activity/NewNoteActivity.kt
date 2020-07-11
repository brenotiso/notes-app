package com.example.notasdobreno.ui.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.notasdobreno.R
import com.example.notasdobreno.database.AppDatabase
import com.example.notasdobreno.database.dao.NoteDao
import com.example.notasdobreno.model.Note
import com.example.notasdobreno.reminder.AlarmHandler
import kotlinx.android.synthetic.main.activity_new_note.*
import java.text.SimpleDateFormat
import java.util.*


class NewNoteActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var noteDao: NoteDao
    private var noteId: Long = 0

    var calendar: Calendar = Calendar.getInstance()

    lateinit var datePickerDialog: DatePickerDialog
    lateinit var timePickerDialog: TimePickerDialog

    var day: Int = 0
    var month: Int = 0
    var year: Int = 0

    var hour: Int = 0
    var minute: Int = 0

    init {
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "notes-database"
        )
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao()

        initialConfigure()
    }

    private fun initialConfigure() {
        datePickerDialog = DatePickerDialog(this@NewNoteActivity, this@NewNoteActivity, year, month, day)
        buttonDate.setOnClickListener {
            datePickerDialog.show()
        }

        timePickerDialog = TimePickerDialog(this@NewNoteActivity, this@NewNoteActivity, hour, minute, true)
        buttonTime.setOnClickListener {
            timePickerDialog.show()
        }

        noteId = intent.getLongExtra("noteId", 0L)
        if (noteId != 0L) {
            configureEditNote()
        }

        buttonCancel.setOnClickListener {
            finish()
        }

        buttonSave.setOnClickListener {
            if (formIsValid()) {
                saveNote()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.msg_miss_field), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureEditNote() {
        val note = noteDao.findById(noteId)

        noteTitle.setText(note.title)
        resume.setText(note.resume)
        description.setText(note.description)

        calendar.time = note.alertAt
        textDatePicked.text = SimpleDateFormat("dd/MM/yyyy").format(note.alertAt)
        datePickerDialog = DatePickerDialog(
            this,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        textTimePicked.text = SimpleDateFormat("HH:mm").format(note.alertAt)
        timePickerDialog =
            TimePickerDialog(this, this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

        when (note.alertFrequency) {
            Note.ONCE -> radioGroup.check(
                R.id.radioButtonOnce
            )
            Note.DAILY -> radioGroup.check(
                R.id.radioButtonDaily
            )
            Note.WEEKLY -> radioGroup.check(
                R.id.radioButtonWeekly
            )
            Note.MONTHLY -> radioGroup.check(
                R.id.radioButtonMonthly
            )
            Note.ANNUALLY -> radioGroup.check(
                R.id.radioButtonAnnually
            )
        }

        toolbar.title = getString(R.string.edit_note)
    }

    private fun formIsValid(): Boolean {
        val frequencySelected = radioGroup.checkedRadioButtonId
        return (noteTitle.text.isNotEmpty() and
                resume.text.isNotEmpty() and
                description.text.isNotEmpty() and
                textDatePicked.text.isNotEmpty() and
                textTimePicked.text.isNotEmpty() and
                (frequencySelected != -1))
    }

    private fun saveNote() {
        val note = createNote()
        if (noteId != 0L) {
            // edição
            noteDao.update(note)
            Toast.makeText(this, getString(R.string.msg_edit_note), Toast.LENGTH_SHORT).show()
        } else {
            // adição
            note.id = noteDao.add(note)
            Toast.makeText(this, getString(R.string.msg_save_note), Toast.LENGTH_SHORT).show()
        }
        AlarmHandler.setAlarm(this, note)
    }

    private fun createNote(): Note {
        val frequencySelected = radioGroup.checkedRadioButtonId
        val radio: RadioButton = findViewById(frequencySelected)

        var createAt = if (noteId == 0L) Date() else noteDao.findById(noteId).createAt


        var alertAt: Calendar = calendar
        alertAt.set(Calendar.YEAR, this.year)
        alertAt.set(Calendar.MONTH, this.month)
        alertAt.set(Calendar.DAY_OF_MONTH, this.day)
        alertAt.set(Calendar.HOUR_OF_DAY, this.hour)
        alertAt.set(Calendar.MINUTE, this.minute)
        alertAt.set(Calendar.SECOND, 0)

        return Note(
            noteId,
            noteTitle.text.toString(),
            resume.text.toString(),
            description.text.toString(),
            createAt,
            alertAt.time,
            frequencyToNum(radio)
        )
    }

    private fun frequencyToNum(radio: RadioButton): Int {
        return when (radio) {
            radioButtonOnce -> Note.ONCE
            radioButtonDaily -> Note.DAILY
            radioButtonWeekly -> Note.WEEKLY
            radioButtonMonthly -> Note.MONTHLY
            radioButtonAnnually -> Note.ANNUALLY
            else -> Note.ONCE
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        this.day = day
        this.month = month
        this.year = year

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        textDatePicked.text = SimpleDateFormat("dd/MM/yyyy").format(calendar.time)

        // atualiza o dialog para caso abra novamente
        datePickerDialog = DatePickerDialog(this@NewNoteActivity, this@NewNoteActivity, year, month, day)
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        textTimePicked.text = SimpleDateFormat("HH:mm").format(calendar.time)

        // atualiza o dialog para caso abra novamente
        timePickerDialog = TimePickerDialog(this@NewNoteActivity, this@NewNoteActivity, hour, minute, true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
