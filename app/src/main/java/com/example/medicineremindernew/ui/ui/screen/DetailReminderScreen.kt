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
    reminderId: String,
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    lansiaViewModel: LansiaViewModel,
    obatViewModel: ObatViewModel,
    onBackClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {}
)
 {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Ambil data dari ViewModel
    val reminder by reminderViewModel.reminderDetail.collectAsState(initial = null)
    val lansiaList by lansiaViewModel.lansiaList.collectAsState(initial = emptyList())
    val obatList by obatViewModel.obatList.collectAsState(initial = emptyList())


    // Load data ketika screen muncul
     LaunchedEffect(reminderId) {
         reminderViewModel.getReminderById(reminderId)
         // Tidak perlu panggil loadLansia dan loadObat, karena real-time Firestore otomatis update
     }


     if (reminder == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // State untuk input
    var selectedLansia by remember { mutableStateOf(reminder?.lansiaId ?: "") }
    var selectedObat by remember { mutableStateOf(reminder?.obatId ?: "") }
    var tanggal by remember { mutableStateOf(reminder?.tanggal ?: "") }
    var waktu by remember { mutableStateOf(reminder?.waktu ?: "") }
    var pengulangan by remember { mutableStateOf(reminder?.pengulangan ?: "") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Edit Reminder", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Pilih Lansia
        Text("Pilih Lansia", fontWeight = FontWeight.Bold)
        DropdownMenuField(
            options = lansiaList.map { it.nama },
            selectedOption = lansiaList.find { it.id == selectedLansia }?.nama ?: "",
            onOptionSelected = { nama ->
                selectedLansia = lansiaList.find { it.nama == nama }?.id ?: ""
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pilih Obat
        Text("Pilih Obat", fontWeight = FontWeight.Bold)
        DropdownMenuField(
            options = obatList.map { it.nama },
            selectedOption = obatList.find { it.id == selectedObat }?.nama ?: "",
            onOptionSelected = { nama ->
                selectedObat = obatList.find { it.nama == nama }?.id ?: ""
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = tanggal,
            onValueChange = { tanggal = it },
            label = { Text("Tanggal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = waktu,
            onValueChange = { waktu = it },
            label = { Text("Waktu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pengulangan,
            onValueChange = { pengulangan = it },
            label = { Text("Pengulangan") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                val updatedReminder = reminder!!.copy(
                    lansiaId = selectedLansia,
                    obatId = selectedObat,
                    tanggal = tanggal,
                    waktu = waktu,
                    pengulangan = pengulangan
                )

                reminderViewModel.updateReminder(updatedReminder) { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Data berhasil diperbarui" else "Gagal memperbarui data"
                        )
                        if (success) onBackClick()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrenMuda),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(OrenMuda))
        ) {
            Text("Update Reminder")
        }

        SnackbarHost(snackbarHostState, modifier = Modifier.padding(16.dp))
    }
}



@Composable
fun AddReminderScreenContent(
    modifier: Modifier = Modifier,
    selectedLansia: String,
    onLansiaSelect: (String) -> Unit,
    selectedObat: String,
    onObatSelect: (String) -> Unit,

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
                ReminderButton(it.nama, { onLansiaSelect(it.id) }, selectedLansia == it.id)
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



