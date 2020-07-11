package com.example.notasdobreno.database.dao

import androidx.room.*
import com.example.notasdobreno.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun all(): List<Note>

    @Query("SELECT * FROM note ORDER BY create_at")
    fun allByDate(): List<Note>

    @Query("SELECT * FROM note WHERE id = (:id)")
    fun findById(id: Long): Note

    @Insert
    fun add(note: Note): Long

    @Update
    fun update(vararg note: Note)

    @Delete
    fun delete(vararg note: Note)

}
