package com.example.medicineremindernew.ui.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicineremindernew.ui.data.model.Lansia
import kotlinx.coroutines.flow.Flow

@Dao
interface LansiaDao {
    @Query("SELECT * FROM lansia_table")
    fun getAll(): Flow<List<Lansia>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lansia: Lansia)

    @Delete
    suspend fun delete(lansia: Lansia)

    @Query("SELECT * FROM lansia_table WHERE id = :id")
    fun getLansiaById(id: Int): Flow<Lansia?>

    @Update
    suspend fun update(lansia: Lansia)

    @Query("SELECT * FROM lansia_table WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<Lansia?>


}

