package com.example.medicineremindernew.ui

import BottomNavigationBar
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.medicineremindernew.ui.data.local.LocalDatabase
import com.example.medicineremindernew.ui.data.network.NetworkUtils
import com.example.medicineremindernew.ui.data.repository.*
import com.example.medicineremindernew.ui.ui.navigation.NavGraph
import com.example.medicineremindernew.ui.ui.theme.BiruTua
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme
import com.example.medicineremindernew.ui.ui.viewmodel.*
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // ✅ Local Database
    private lateinit var localDatabase: LocalDatabase
    private lateinit var networkUtils: NetworkUtils

    // ✅ Hybrid Repositories
    private lateinit var hybridReminderRepository: HybridReminderRepository
    private lateinit var hybridLansiaRepository: HybridLansiaRepository
    private lateinit var hybridObatRepository: HybridObatRepository
    private lateinit var hybridKunjunganRepository: HybridKunjunganRepository

    // ✅ Repository utama (Firestore base)
    private val firestoreRepository = FirestoreRepository()

    // ✅ Repository untuk tiap data
    private val lansiaRepository by lazy { LansiaRepository(firestoreRepository) }
    private val obatRepository by lazy { ObatRepository(firestoreRepository) }
    private val reminderRepository by lazy { ReminderRepository(firestoreRepository) }
    private val kunjunganRepository by lazy { KunjunganRepository(firestoreRepository) }

    // ✅ Hybrid ViewModels (akan diinisialisasi manual)
    private lateinit var hybridObatViewModel: HybridObatViewModel
    private lateinit var hybridLansiaViewModel: HybridLansiaViewModel
    private lateinit var hybridReminderViewModel: HybridReminderViewModel
    private lateinit var hybridKunjunganViewModel: HybridKunjunganViewModel

    // ✅ ViewModels standar
    private val lansiaViewModel: LansiaViewModel by viewModels {
        LansiaViewModelFactory(lansiaRepository)
    }

    private val obatViewModel: ObatViewModel by viewModels {
        ObatViewModelFactory(obatRepository)
    }

    private val reminderViewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(reminderRepository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // ✅ Penting!

        // ✅ Inisialisasi local database & network utils
        localDatabase = LocalDatabase.getDatabase(this)
        networkUtils = NetworkUtils(this)

        // ✅ Inisialisasi hybrid repositories
        hybridReminderRepository = HybridReminderRepository(
            context = this,
            reminderRepository = reminderRepository,
            localDao = localDatabase.reminderDao(),
            networkUtils = networkUtils
        )

        hybridLansiaRepository = HybridLansiaRepository(
            lansiaRepository,
            localDatabase.lansiaDao(),
            networkUtils
        )

        hybridObatRepository = HybridObatRepository(
            obatRepository,
            localDatabase.obatDao(),
            networkUtils
        )

        hybridKunjunganRepository = HybridKunjunganRepository(
            context = this,
            kunjunganRepository = kunjunganRepository,
            localDao = localDatabase.kunjunganDao(),
            networkUtils = networkUtils
        )

        // ✅ Inisialisasi Hybrid ViewModels setelah repositories siap
        hybridObatViewModel = HybridObatViewModelFactory(hybridObatRepository)
            .create(HybridObatViewModel::class.java)

        hybridLansiaViewModel = HybridLansiaViewModelFactory(hybridLansiaRepository)
            .create(HybridLansiaViewModel::class.java)

        hybridReminderViewModel = HybridReminderViewModelFactory(hybridReminderRepository)
            .create(HybridReminderViewModel::class.java)

        hybridKunjunganViewModel = HybridKunjunganViewModelFactory(this, firestoreRepository)
            .create(HybridKunjunganViewModel::class.java)

        // ✅ Observer perubahan koneksi untuk auto-sync
        lifecycleScope.launch {
            networkUtils.observeNetworkChanges().collect { isConnected ->
                if (isConnected) {
                    Log.d("MainActivity", "Internet tersedia, mulai sinkronisasi...")
                    syncAllPendingData()
                } else {
                    Log.d("MainActivity", "Internet tidak tersedia, mode offline")
                }
            }
        }

        // ✅ Pastikan alarm tidak mati karena Battery Optimization
        requestIgnoreBatteryOptimization()

        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Medicine Reminder") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = BiruTua.copy(alpha = 1.0f),
                                titleContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        obatViewModel = hybridObatViewModel,
                        lansiaViewModel = hybridLansiaViewModel,
                        reminderViewModel = hybridReminderViewModel,
                        kunjunganViewModel = hybridKunjunganViewModel,
                        hybridReminderRepository = hybridReminderRepository,
                        hybridLansiaRepository = hybridLansiaRepository,
                        hybridObatRepository = hybridObatRepository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // ✅ Sinkronisasi semua data yang pending
    private suspend fun syncAllPendingData() {
        try {
            hybridReminderRepository.syncPendingData()
            hybridLansiaRepository.syncPendingData()
            hybridObatRepository.syncPendingData()
            Log.d("MainActivity", "Sinkronisasi selesai")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saat sinkronisasi", e)
        }
    }

    // ✅ Minta user agar mengizinkan aplikasi mengabaikan battery optimization
    private fun requestIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk alarm pengingat"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
