package com.example.medicineremindernew.ui

import BottomNavigationBar
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.local.SessionManager
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import com.example.medicineremindernew.ui.ui.navigation.NavGraph
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModelFactory
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModel


class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val userEmail = sessionManager.getEmail()

        // Cek izin seperti sebelumnya...
        // ...

        val database = Room.databaseBuilder(
            applicationContext,
            ObatDatabase::class.java,
            "obat_database"
        ).fallbackToDestructiveMigration().build()

        val obatRepository = ObatRepository(database.obatDao())
        val lansiaRepository = LansiaRepository(database.lansiaDao())
        val reminderRepository = ReminderRepository(database.reminderDao())

        val obatViewModel = ObatViewModel(obatRepository)
        val lansiaViewModel = LansiaViewModel(lansiaRepository)
        val reminderViewModel = ReminderViewModel(reminderRepository)

        val authViewModelFactory = AuthViewModelFactory(application)
        val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

        // ✅ Jika ada session user, langsung anggap login
        if (userEmail != null) {
            authViewModel.setLoginSuccessFromSession(userEmail)
        }

        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()
                val loginSuccess by authViewModel.loginSuccess.collectAsState()

                if (loginSuccess) {
                    Scaffold(
                        // ini utk logout atau kemmbali
                        topBar = {
                            TopAppBar(
                                title = { Text("Medicine Reminder") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        authViewModel.logout(this@MainActivity)

                                        // Navigasi ke login
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true } // Hapus semua dari backstack
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Logout"
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = { BottomNavigationBar(navController) }) {
                        NavGraph(
                            navController = navController,
                            obatViewModel = obatViewModel,
                            lansiaViewModel = lansiaViewModel,
                            reminderViewModel = reminderViewModel,
                            authViewModel = authViewModel
                        )
                    }
                } else {
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
