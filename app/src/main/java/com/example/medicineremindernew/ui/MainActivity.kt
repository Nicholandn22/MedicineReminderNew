package com.example.medicineremindernew.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.ui.components.BottomNavigationBar
import com.example.medicineremindernew.ui.ui.navigation.NavGraph
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi database
        val database = Room.databaseBuilder(
            applicationContext,
            ObatDatabase::class.java,
            "obat_database"
        ).build()

        // 2. Inisialisasi repository
        val repository = ObatRepository(database.obatDao())

        // 3. Inisialisasi ViewModel manual
        val obatViewModel = ObatViewModel(repository)

        // 4. Pasang ke UI
        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) {
                    // 5. Kirim viewModel ke NavGraph
                    NavGraph(navController = navController, obatViewModel = obatViewModel)
                }
            }
        }
    }
}
