package com.example.medicineremindernew.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import com.example.medicineremindernew.ui.ui.components.BottomNavigationBar
import com.example.medicineremindernew.ui.ui.navigation.NavGraph
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModelFactory
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi database Room
        val database = Room.databaseBuilder(
            applicationContext,
            ObatDatabase::class.java,
            "obat_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        // 2. Inisialisasi repository
        val obatRepository = ObatRepository(database.obatDao())
        val lansiaRepository = LansiaRepository(database.lansiaDao())
        val reminderRepository = ReminderRepository(database.reminderDao())

        // 3. Inisialisasi ViewModel secara manual (non-Hilt)
        val obatViewModel = ObatViewModel(obatRepository)
        val lansiaViewModel = LansiaViewModel(lansiaRepository)
        val reminderViewModel = ReminderViewModel(reminderRepository)

        // ✅ INI DIPINDAH KE SINI
        val authViewModelFactory = AuthViewModelFactory(application) // ✅ application adalah android.app.Application
        val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

        // 4. Pasang ke UI
        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) {
                    NavGraph(
                        navController = navController,
                        obatViewModel = obatViewModel,
                        lansiaViewModel = lansiaViewModel,
                        reminderViewModel = reminderViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

