// AddReminderScreen.kt
package com.example.medicineremindernew.ui.ui.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicineremindernew.R
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.model.Reminder
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import com.example.medicineremindernew.ui.ui.theme.AbuMenu
import com.example.medicineremindernew.ui.ui.theme.PutihKolom
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModelFactory
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModelFactory
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModelFactory
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun AddReminderScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    obatViewModel: ObatViewModel,
    lansiaViewModel: LansiaViewModel,
    reminderViewModel: ReminderViewModel
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = remember { ObatDatabase.getDatabase(application) }

    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(ReminderRepository(db.reminderDao()))
    )

    val coroutineScope = rememberCoroutineScope()

    val obatViewModel: ObatViewModel = viewModel(
        factory = ObatViewModelFactory(ObatRepository(db.obatDao()))
    )

    val lansiaViewModel: LansiaViewModel = viewModel(
        factory = LansiaViewModelFactory(LansiaRepository(db.lansiaDao()))
    )

    val lansiaList = lansiaViewModel.getAllLansia.collectAsState(initial = emptyList()).value
    val obatList = obatViewModel.allObat.collectAsState(initial = emptyList()).value

    var selectedLansia by remember { mutableStateOf<Int?>(null) }
    var selectedObat by remember { mutableStateOf<Int?>(null) }
    var tanggal by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }

    val pengulanganOptions = listOf("Harian", "Mingguan", "Bulanan")
    val nadaDeringOptions = listOf("Nada 1", "Nada 2", "Nada 3")

    var selectedPengulangan by remember { mutableStateOf(pengulanganOptions.first()) }
    var selectedNadaDering by remember { mutableStateOf(nadaDeringOptions.first()) }

    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x0FFFFFFF))
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFFFC5007)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart).padding(top = 25.dp)) {
                    Icon(
                        painter = painterResource(id = back_white),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text("Tambah Pengingat", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(top = 15.dp))
            }

            // Section: Pengingat
            SectionTitle("Pengingat")
            CardSection {
                val context = LocalContext.current
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
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF6F00), shape = RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xBDBDBD),
                        contentColor = Color(0xFFFF6F00)
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
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFF6F00), shape = RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xBDBDBD),
                    contentColor = Color(0xFFFF6F00)
                ),
                shape = RoundedCornerShape(30.dp)
                ) {
                    Text(text = if (waktu.isEmpty()) "Pilih Waktu" else "Waktu: $waktu")
                }

                Spacer(modifier = Modifier.height(4.dp))

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

            // Section: Pasien
            SectionWithAddButton("Pasien", navController = navController)
            CardSection {
                if (lansiaList.isEmpty()) {
                    Text("Belum ada data lansia")
                } else {
                    lansiaList.forEach { lansia ->
                        ReminderButton(
                            text = lansia.name,
                            onClick = { selectedLansia = lansia.id },
                            isSelected = selectedLansia == lansia.id
                        )

                    }
                }
            }

            // Section: Obat
            SectionWithAddButton("List Obat", navController = navController)
            CardSection {
                if (obatList.isEmpty()) {
                    Text("Belum ada data obat")
                } else {
                    obatList.forEach { obat ->
                        ReminderButton(
                            text = obat.nama,
                            onClick = { selectedObat = obat.id },
                            isSelected = selectedObat == obat.id
                        )

                    }
                }
            }

            // Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Tombol Save
                Button(
                    onClick = {
                        if (selectedLansia != null && selectedObat != null && tanggal.isNotEmpty() && waktu.isNotEmpty()) {
                            val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())

                            val reminder = Reminder(
                                obatId = selectedObat!!,
                                lansiaId = selectedLansia!!,
                                tanggal = java.sql.Date(formatterDate.parse(tanggal)!!.time),
                                waktu = Time(formatterTime.parse(waktu)!!.time),
                                pengulangan = selectedPengulangan
                            )

                            // Insert dan jadwalkan alarm
                            reminderViewModel.insertAndSchedule(reminder, context) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Reminder berhasil disimpan dan alarm dijadwalkan")
                                }
                                onSaveClick()
                                navController.popBackStack() // 👈 ini akan kembali ke halaman sebelumnya

                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Harap lengkapi semua data")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x0FFFFFFF),
                        contentColor = Color(0xFFFF6F00)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(1.dp, Color(0xFFFF6F00), shape = RoundedCornerShape(35.dp)),
                ){
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
                        containerColor = Color(0x0FFFFFFF),
                        contentColor = Color(0xFFFF6F00)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .border(1.dp, Color(0xFFFF6F00), shape = RoundedCornerShape(35.dp)),
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
            color = Color.Black
        )
        Button(
            onClick = { navController.navigate(route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.plus_black),
                contentDescription = "Tambah $title",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
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
        Button(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFFF6F00), shape = RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xBDBDBD), // abu-abu
                contentColor = Color(0xFFFF6F00)    // orange terang
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(selectedOption)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onOptionSelected(it)
                        expanded = false
                    }
                )
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
    val backgroundColor = if (isSelected) Color(0xFFFF6F00) else PutihKolom // Oren
    val contentColor = if (isSelected) Color.White else Color.Black

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
                    tint = Color.White
                )
            }
        }
    }
}

