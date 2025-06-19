package com.example.medicineremindernew.ui.ui.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicineremindernew.R
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailReminderScreen(
    reminderId: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = remember { ObatDatabase.getDatabase(application) }

    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(ReminderRepository(db.reminderDao()))
    )
    val obatViewModel: ObatViewModel = viewModel(
        factory = ObatViewModelFactory(ObatRepository(db.obatDao()))
    )
    val lansiaViewModel: LansiaViewModel = viewModel(
        factory = LansiaViewModelFactory(LansiaRepository(db.lansiaDao()))
    )

    val coroutineScope = rememberCoroutineScope()
    val lansiaList = lansiaViewModel.getAllLansia.collectAsState(initial = emptyList()).value
    val obatList = obatViewModel.allObat.collectAsState(initial = emptyList()).value
    val reminder by reminderViewModel.getReminderById(reminderId).collectAsState(initial = null)

    var selectedLansia by remember { mutableStateOf<Int?>(null) }
    var selectedObat by remember { mutableStateOf<Int?>(null) }
    var tanggal by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }
    val pengulanganOptions = listOf("Harian", "Mingguan", "Bulanan")
    val nadaDeringOptions = listOf("Nada 1", "Nada 2", "Nada 3")
    var selectedPengulangan by remember { mutableStateOf(pengulanganOptions.first()) }
    var selectedNadaDering by remember { mutableStateOf(nadaDeringOptions.first()) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(reminder) {
        reminder?.let {
            selectedLansia = it.lansiaId
            selectedObat = it.obatId
            tanggal = it.tanggal.toString()
            waktu = it.waktu.toString().substring(0, 5)
            selectedPengulangan = it.pengulangan
        }
    }

    if (reminder != null) {
        AddReminderScreenContent(
            modifier = modifier,
            selectedLansia = selectedLansia,
            onLansiaSelect = { selectedLansia = it },
            selectedObat = selectedObat,
            onObatSelect = { selectedObat = it },
            tanggal = tanggal,
            onTanggalChange = { tanggal = it },
            waktu = waktu,
            onWaktuChange = { waktu = it },
            selectedPengulangan = selectedPengulangan,
            onPengulanganChange = { selectedPengulangan = it },
            selectedNadaDering = selectedNadaDering,
            onNadaDeringChange = { selectedNadaDering = it },
            lansiaList = lansiaList,
            obatList = obatList,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onSaveClick = {
                if (selectedLansia != null && selectedObat != null && tanggal.isNotEmpty() && waktu.isNotEmpty()) {
                    val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val updatedReminder = reminder!!.copy(
                        lansiaId = selectedLansia!!,
                        obatId = selectedObat!!,
                        tanggal = java.sql.Date(formatterDate.parse(tanggal)!!.time),
                        waktu = Time(formatterTime.parse(waktu)!!.time),
                        pengulangan = selectedPengulangan
                    )
                    reminderViewModel.updateAndSchedule(updatedReminder, context) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Reminder berhasil diperbarui")
                        }
                        onUpdateClick()
                        navController.navigate("login")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Harap lengkapi semua data")
                    }
                }
            },
            navController = navController
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AddReminderScreenContent(
    modifier: Modifier = Modifier,
    selectedLansia: Int?,
    onLansiaSelect: (Int) -> Unit,
    selectedObat: Int?,
    onObatSelect: (Int) -> Unit,
    tanggal: String,
    onTanggalChange: (String) -> Unit,
    waktu: String,
    onWaktuChange: (String) -> Unit,
    selectedPengulangan: String,
    onPengulanganChange: (String) -> Unit,
    selectedNadaDering: String,
    onNadaDeringChange: (String) -> Unit,
    lansiaList: List<Lansia>,
    obatList: List<Obat>,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onTanggalChange(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember {
        android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onWaktuChange(String.format("%02d:%02d", hourOfDay, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(
                    painter = painterResource(id = R.drawable.add_file),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp).size(32.dp)
                )
            }
            Text("Pengingat", color = Color.White, fontSize = 20.sp)
        }

        SectionTitle("Pengingat")
        CardSection {
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrenMuda),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(OrenMuda))
            ) {
                Text(if (tanggal.isEmpty()) "Pilih Tanggal" else "Tanggal: $tanggal")
            }

            OutlinedButton(
                onClick = { timePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrenMuda),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(OrenMuda))
            ) {
                Text(if (waktu.isEmpty()) "Pilih Waktu" else "Waktu: $waktu")
            }

            Text("Pengulangan", fontWeight = FontWeight.Bold)
            DropdownMenuField(listOf("Harian", "Mingguan", "Bulanan"), selectedPengulangan) {
                onPengulanganChange(it)
            }

            Text("Nada Dering", fontWeight = FontWeight.Bold)
            DropdownMenuField(listOf("Nada 1", "Nada 2", "Nada 3"), selectedNadaDering) {
                onNadaDeringChange(it)
            }
        }

        SectionWithAddButton("Pasien", navController)
        CardSection {
            if (lansiaList.isEmpty()) Text("Belum ada data lansia")
            else lansiaList.forEach {
                ReminderButton(it.name, { onLansiaSelect(it.id) }, selectedLansia == it.id)
            }
        }

        SectionWithAddButton("List Obat", navController)
        CardSection {
            if (obatList.isEmpty()) Text("Belum ada data obat")
            else obatList.forEach {
                ReminderButton(it.nama, { onObatSelect(it.id) }, selectedObat == it.id)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onSaveClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrenMuda),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(OrenMuda)),
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text("Simpan")
            }
        }

        SnackbarHost(snackbarHostState, modifier = Modifier.fillMaxWidth().padding(50.dp))
    }
}