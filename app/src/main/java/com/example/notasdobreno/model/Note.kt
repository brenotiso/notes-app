package com.example.notasdobreno.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.notasdobreno.converter.DateConverter
import java.util.*

@Entity
@TypeConverters(DateConverter::class)
class Note (
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var title: String,
    var resume: String,
    var description: String,
    @ColumnInfo(name = "create_at") var  createAt: Date,
    @ColumnInfo(name = "alert_at") var  alertAt: Date,
    @ColumnInfo(name = "alert_frequency") var  alertFrequency: Int
) {
    companion object {
        const val ONCE = 0
        const val DAILY = 1
        const val WEEKLY = 2
        const val MONTHLY = 3
        const val ANNUALLY = 4
    }
}