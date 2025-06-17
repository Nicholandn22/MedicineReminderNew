package com.example.medicineremindernew.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

        // ✅ Minta izin notifikasi (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        // ✅ Cek apakah user sudah mengaktifkan izin exact alarm (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    this,
                    "Izin SCHEDULE_EXACT_ALARM belum diberikan. Harap aktifkan secara manual di pengaturan aplikasi.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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

        // 4. Inisialisasi AuthViewModel
        val authViewModelFactory = AuthViewModelFactory(application)
        val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

        // 5. Pasang ke UI
        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()
                val loginSuccess by authViewModel.loginSuccess.collectAsState()

                if (loginSuccess) {
                    // ✅ Bottom bar hanya muncul kalau sudah login
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
                } else {
                    // ✅ Jangan pakai BottomNavigationBar
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
