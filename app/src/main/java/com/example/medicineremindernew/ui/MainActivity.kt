package com.example.medicineremindernew.ui

import BottomNavigationBar
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.medicineremindernew.R.drawable.sign_out
import com.example.medicineremindernew.ui.data.local.SessionManager
import com.example.medicineremindernew.ui.data.repository.FirestoreRepository
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import com.example.medicineremindernew.ui.ui.navigation.NavGraph
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
import com.example.medicineremindernew.ui.ui.viewmodel.*
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    // ✅ Repository utama (Firestore base)
    private val firestoreRepository = FirestoreRepository()

    // ✅ Repository untuk tiap data
    private val lansiaRepository by lazy { LansiaRepository(firestoreRepository) }
    private val obatRepository by lazy { ObatRepository(firestoreRepository) }
    private val reminderRepository by lazy { ReminderRepository(firestoreRepository) }

    // ✅ ViewModels dengan Factory
    private val lansiaViewModel: LansiaViewModel by viewModels {
        LansiaViewModelFactory(lansiaRepository)
    }

    private val obatViewModel: ObatViewModel by viewModels {
        ObatViewModelFactory(obatRepository)
    }

    private val reminderViewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(reminderRepository)
    }

//    private val authViewModel: AuthViewModel by viewModels {
//        AuthViewModelFactory(application)
//    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // ✅ Penting!

        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Medicine Reminder") }
                        )
                    },
                    bottomBar = { BottomNavigationBar(navController) }
                ) {
                    NavGraph(
                        navController = navController,
                        obatViewModel = obatViewModel,
                        lansiaViewModel = lansiaViewModel,
                        reminderViewModel = reminderViewModel
//                        authViewModel = null // ✅ Kalau tidak dipakai lagi
                    )
                }
            }
        }
    }

}

