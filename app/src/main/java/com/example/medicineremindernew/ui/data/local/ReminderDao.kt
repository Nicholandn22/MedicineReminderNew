package com.example.medicineremindernew.ui.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicineremindernew.ui.data.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.sql.Date

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("""
    SELECT * FROM reminder_table 
    WHERE tanggal >= :currentDate
    ORDER BY tanggal ASC, waktu ASC
""")
    fun getAll(currentDate: Date): Flow<List<Reminder>>





    @Query("SELECT * FROM reminder_table WHERE id = :id")
    fun getReminderById(id: Int): Flow<Reminder?>

    @Update
    suspend fun update(reminder: Reminder)

    @Query("SELECT * FROM reminder_table")
    fun getAllRemindersRaw(): Flow<List<Reminder>>





}
