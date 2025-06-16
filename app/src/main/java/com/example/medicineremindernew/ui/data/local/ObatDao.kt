package com.example.medicineremindernew.ui.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicineremindernew.ui.data.model.Obat
import kotlinx.coroutines.flow.Flow

@Dao
interface ObatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obat: Obat)

    @Delete
    suspend fun delete(obat: Obat)

    @Query("SELECT * FROM obat_table ORDER BY id DESC")
    fun getAllObat(): Flow<List<Obat>>

    @Query("SELECT * FROM obat_table WHERE id = :id LIMIT 1")
    fun getObatById(id: Int): Flow<Obat?>

    @Update
    suspend fun update(obat: Obat)

}
