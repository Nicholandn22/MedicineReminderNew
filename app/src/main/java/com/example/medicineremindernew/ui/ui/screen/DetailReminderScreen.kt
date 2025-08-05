package com.example.medicineremindernew.ui.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.example.medicineremindernew.R
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.ui.theme.BiruAgakTua
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.alarm.scheduleAlarm
import com.example.medicineremindernew.ui.alarm.cancelAlarm
import com.example.medicineremindernew.ui.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailReminderScreen(
    reminderId: String,
    navController: NavController,
    reminderViewModel: HybridReminderViewModel,
    lansiaViewModel: HybridLansiaViewModel,
    obatViewModel: HybridObatViewModel,
    onBackClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Ambil data dari ViewModel
    val reminder by reminderViewModel.reminderDetail.collectAsState(initial = null)
    val lansiaList by lansiaViewModel.lansiaList.collectAsState(initial = emptyList())
    val obatList by obatViewModel.obatList.collectAsState(initial = emptyList())

    // Buat pengulangan
    val pengulanganOptions = listOf("Harian", "Mingguan", "Bulanan")

    // Load data ketika screen muncul
    LaunchedEffect(reminderId) {
        reminderViewModel.getReminderById(reminderId)
        // Tidak perlu panggil loadLansia dan loadObat, karena real-time Firestore otomatis update
    }

    // Variabel buat ngedit tgl
    val calendar = remember { Calendar.getInstance() }
    val context = LocalContext.current

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

    // Buat ngedit tgl biar sama kayak pas AddReminder
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                tanggal = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Buat ngedit waktu biar sama kayak pas AddReminder
    val timePickerDialog = remember {
        android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                waktu = String.format("%02d:%02d", hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ✅ Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(16.dp)
                .background(BiruAgakTua.copy(alpha = 1.0f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(
                    painter = painterResource(id = back_white),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text("Update Pengingat", color = Color.White, fontSize = 20.sp)
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Edit Lansia
                Text("Pilih Lansia", fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = lansiaList.map { it.nama },
                    selectedOption = lansiaList.find { it.id == selectedLansia }?.nama ?: "",
                    onOptionSelected = { nama ->
                        selectedLansia = lansiaList.find { it.nama == nama }?.id ?: ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Obat
                Text("Pilih Obat", fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = obatList.map { it.nama },
                    selectedOption = obatList.find { it.id == selectedObat }?.nama ?: "",
                    onOptionSelected = { nama ->
                        selectedObat = obatList.find { it.nama == nama }?.id ?: ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Tanggal
//                OutlinedTextField(
//                    value = tanggal,
//                    onValueChange = { tanggal = it },
//                    label = { Text("Tanggal") },
//                    modifier = Modifier.fillMaxWidth()
//                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() } // klik di seluruh box
                ) {
                    OutlinedTextField(
                        value = tanggal,
                        textStyle = androidx.compose.ui.text.TextStyle(color = BiruMuda.copy(alpha = 1.0f)),
                        onValueChange = {}, // kosongin supaya gak bisa diubah manual
                        label = { Text("Tanggal") },
                        readOnly = true, // tetap tampil sebagai read-only
                        enabled = false,
                        colors = TextFieldDefaults.colors(
                            disabledLabelColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Waktu
//                OutlinedTextField(
//                    value = waktu,
//                    onValueChange = { waktu = it },
//                    label = { Text("Waktu") },
//                    modifier = Modifier.fillMaxWidth()
//                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { timePickerDialog.show() } // klik di seluruh box
                ) {
                    OutlinedTextField(
                        value = waktu,
                        textStyle = androidx.compose.ui.text.TextStyle(color = BiruMuda.copy(alpha = 1.0f)),
                        onValueChange = {}, // kosongin supaya gak bisa diubah manual
                        label = { Text("Waktu") },
                        readOnly = true, // tetap tampil sebagai read-only
                        enabled = false,
                        colors = TextFieldDefaults.colors(
                            disabledLabelColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Pengulangan
//                OutlinedTextField(
//                    value = pengulangan,
//                    onValueChange = { pengulangan = it },
//                    label = { Text("Pengulangan") },
//                    modifier = Modifier.fillMaxWidth()
//                )
                Text("Pengulangan", fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = pengulanganOptions,
                    selectedOption = pengulangan,
                    onOptionSelected = { pengulangan = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        // ✅ Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
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
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruMuda.copy(alpha = 1.0f)),
            ) {
                Text("Update")
            }
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruMuda.copy(alpha = 1.0f))
            ) {
                Text("Cancel")
            }
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruMuda.copy(alpha = 1.0f)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(
                            BiruMuda.copy(
                                alpha = 1.0f
                            )
                        )
                    )
                ) {
                    Text(if (tanggal.isEmpty()) "Pilih Tanggal" else "Tanggal: $tanggal")
                }

                OutlinedButton(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruMuda.copy(alpha = 1.0f)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(
                            BiruMuda.copy(
                                alpha = 1.0f
                            )
                        )
                    )
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruMuda.copy(alpha = 1.0f)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(
                            BiruMuda.copy(
                                alpha = 1.0f
                            )
                        )
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("Simpan")
                }
            }

            SnackbarHost(snackbarHostState, modifier = Modifier.fillMaxWidth().padding(50.dp))
        }
    }




