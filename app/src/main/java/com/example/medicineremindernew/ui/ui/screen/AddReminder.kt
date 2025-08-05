// AddReminderScreen.kt
package com.example.medicineremindernew.ui.ui.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medicineremindernew.R
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.data.model.Reminder
import com.example.medicineremindernew.ui.ui.theme.AbuMenu
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.ui.theme.BiruAgakTua
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.viewmodel.HybridLansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID


@Composable
fun AddReminderScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    lansiaList: StateFlow<List<Lansia>>,
    obatList: StateFlow<List<Obat>>,
    reminderViewModel: HybridReminderViewModel,
    obatViewModel: HybridObatViewModel,
    lansiaViewModel: HybridLansiaViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedLansia by remember { mutableStateOf<String?>(null) }
    var selectedObat by remember { mutableStateOf<String?>(null) }
    var tanggal by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }

    val pengulanganOptions = listOf("Harian", "Mingguan", "Bulanan")
    val nadaDeringOptions = listOf("Nada 1", "Nada 2", "Nada 3")

    var selectedPengulangan by remember { mutableStateOf(pengulanganOptions.first()) }
    var selectedNadaDering by remember { mutableStateOf(nadaDeringOptions.first()) }

    val lansiaListState = lansiaList.collectAsState()
    val obatListState = obatList.collectAsState()

    Box(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ✅ Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                Text("Tambah Pengingat", color = Color.White, fontSize = 20.sp)
            }

            val biru = BiruMuda.copy(alpha = 1.0f)

            // ✅ Section: Pilih Tanggal & Waktu
            SectionTitle("Pengingat")
            CardSection {
                val calendar = remember { Calendar.getInstance() }

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

                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth().border(1.dp, biru, shape = RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BiruMuda.copy(alpha = 0.0f),
                        contentColor = biru
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(text = if (tanggal.isEmpty()) "Pilih Tanggal" else "Tanggal: $tanggal")
                }

                Spacer(modifier = Modifier.height(8.dp))

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

                Button(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth().border(1.dp, biru, shape = RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BiruMuda.copy(alpha = 0.0f),
                        contentColor = biru
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(text = if (waktu.isEmpty()) "Pilih Waktu" else "Waktu: $waktu")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Pengulangan", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                DropdownMenuField(pengulanganOptions, selectedPengulangan) {
                    selectedPengulangan = it
                }
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Nada Dering", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                DropdownMenuField(nadaDeringOptions, selectedNadaDering) {
                    selectedNadaDering = it
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // ✅ Section Lansia
            SectionWithAddButton("Pasien", navController = navController)
            CardSection {
                if (lansiaListState.value.isEmpty()) {
                    Text("Belum ada data lansia")
                } else {
                    lansiaListState.value.forEach { lansia ->
                        ReminderButton(
                            text = lansia.nama,
                            onClick = { selectedLansia = lansia.id },
                            isSelected = selectedLansia == lansia.id
                        )
                    }
                }
            }

            // ✅ Section Obat
            SectionWithAddButton("List Obat", navController = navController)
            CardSection {
                if (obatListState.value.isEmpty()) {
                    Text("Belum ada data obat")
                } else {
                    obatListState.value.forEach { obat ->
                        ReminderButton(
                            text = obat.nama,
                            onClick = { selectedObat = obat.id },
                            isSelected = selectedObat == obat.id
                        )
                    }
                }
            }

            // ✅ Buttons Save & Clear
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(left = 0.dp, top = 40.dp, right = 0.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Tombol Save
                Button(
                    onClick = {
                        if (selectedLansia != null && selectedObat != null && tanggal.isNotEmpty() && waktu.isNotEmpty()) {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                            val reminder = Reminder(
                                id = UUID.randomUUID().toString(),  // ✅ ID hanya generate sekali disini!
                                obatId = selectedObat!!,
                                lansiaId = selectedLansia!!,
                                tanggal = tanggal,       // Simpan tanggal sebagai String
                                waktu = waktu,           // Simpan waktu sebagai String
                                pengulangan = selectedPengulangan
                                // userId hilangkan karena model Anda tidak punya field ini
                            )

                            // ✅ Simpan ke Firestore via ViewModel
                            reminderViewModel.addReminder(reminder) { success ->
                                coroutineScope.launch {
                                    if (success) {
                                        // ✅ Jadwalkan alarm setelah reminder berhasil disimpan
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
                                            if (!alarmManager.canScheduleExactAlarms()) {
                                                val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startActivity(intent)
                                                return@launch
                                            }
                                        }

                                        val dateTimeString = "$tanggal $waktu"
                                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                        val date = sdf.parse(dateTimeString)
                                        val timeInMillis = date?.time ?: 0L

                                        // ✅ Panggil fungsi untuk set alarm
                                        com.example.medicineremindernew.ui.alarm.scheduleAlarm(
                                            context,
                                            reminder.id,  // ✅ Udah pasti ada nilainya
                                            timeInMillis
                                        )
                                        snackbarHostState.showSnackbar("Reminder berhasil disimpan & alarm dijadwalkan")
                                        navController.popBackStack()
                                    }
                                    else {
                                        snackbarHostState.showSnackbar("Gagal menyimpan reminder")
                                    }
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Harap lengkapi semua data")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BiruMuda.copy(alpha = 0.0f),
                        contentColor = biru
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(1.dp, biru, shape = RoundedCornerShape(35.dp)),
                ) {
                    Text("Save")
                }

                // Tombol Clear
                Button(
                    onClick = {
                        selectedLansia = null
                        selectedObat = null
                        tanggal = ""
                        waktu = ""
                        selectedPengulangan = pengulanganOptions.first()
                        selectedNadaDering = nadaDeringOptions.first()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Form telah direset")
                        }
                        onClearClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BiruMuda.copy(alpha = 0.0f),
                        contentColor = biru
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .border(1.dp, biru, shape = RoundedCornerShape(35.dp)),
                ) {
                    Text("Clear")
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
            )
        }
    }
}


// ====================== Helper Composables ======================

@Composable
fun SectionTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AbuMenu)
            .padding(16.dp)
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionWithAddButton(
    title: String,
    navController: NavController
) {
    val route = when (title) {
        "List Obat" -> "addObat"
        "Pasien" -> "addlansia"
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AbuMenu)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Button(
            onClick = { navController.navigate(route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = BiruAgakTua.copy(alpha = 1.0f),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.plus_black),
                contentDescription = "Tambah $title",
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun CardSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(8.dp),
//        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun DropdownMenuField(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedOption.ifEmpty { "Pilih" }, color = BiruMuda.copy(alpha = 1.0f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun ReminderButton(
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) BiruMuda.copy(alpha = 1.0f) else BiruMuda.copy(alpha = 0.0f)
    val contentColor = if (isSelected) Color.Black else Color.White

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text)
            if (isSelected) {
                Icon(
                    painter = painterResource(id = android.R.drawable.checkbox_on_background),
                    contentDescription = "Selected",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified // ✅ ini biar pakai warna asli
                )
            }
        }
    }
}

