package com.example.medicineremindernew.ui.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicineremindernew.ui.data.local.LocalDatabase
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.model.Riwayat
import com.example.medicineremindernew.ui.data.network.NetworkUtils
import com.example.medicineremindernew.ui.data.repository.HybridRiwayatRepository
import com.example.medicineremindernew.ui.data.repository.RiwayatRepository
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.theme.BiruTua
import com.example.medicineremindernew.ui.ui.theme.Krem
import com.example.medicineremindernew.ui.ui.viewmodel.HybridLansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridReminderViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridRiwayatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridRiwayatViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.saveable.rememberSaveable

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    reminderViewModel: HybridReminderViewModel,
    lansiaViewModel: HybridLansiaViewModel,
    obatViewModel: HybridObatViewModel
) {
    val reminders by reminderViewModel.reminderList.collectAsState()
    val lansiaList by lansiaViewModel.lansiaList.collectAsState()
    val obatList by obatViewModel.obatList.collectAsState()
    val context = LocalContext.current

    val warnaKrem = Krem.copy(alpha = 1.0f)
    val warnaBiru = BiruTua.copy(alpha = 1.0f)

    // Waktu sekarang
    val now = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    // Filter hanya reminder yang >= sekarang
    val filteredReminders = reminders
        .mapNotNull { reminder ->
            try {
                val dateTime = LocalDateTime.parse("${reminder.tanggal} ${reminder.waktu}", formatter)
                Pair(reminder, dateTime)
            } catch (e: Exception) {
                Log.e("ReminderParse", "Gagal parsing: ${reminder.tanggal} ${reminder.waktu}")
                null
            }
        }
        .sortedBy { (_, dateTime) -> dateTime } // tetap diurutkan
        .map { (reminder, _) -> reminder }

    // Reminder terdekat
    val reminderTerdekat = filteredReminders
        .sortedWith(compareBy({ it.tanggal }, { it.waktu }))
        .firstOrNull()


    var showDeleteDialog by remember { mutableStateOf(false) }
    var reminderToDelete by remember { mutableStateOf<String?>(null) }

    val db = LocalDatabase.getDatabase(context) // sesuaikan dengan nama DB-mu
    val dao = db.riwayatDao()
    val networkUtils = NetworkUtils(context)
// ✅ bikin Firestore Repository dulu
// ✅ bikin Firestore Repository dulu
     val firestoreRepo = RiwayatRepository() // RiwayatRepository tidak perlu argumen
    val repository = HybridRiwayatRepository(
        riwayatRepository = firestoreRepo,
        localDao = dao,
        networkUtils = networkUtils,
        context = context
    )

    val riwayatViewModel: HybridRiwayatViewModel = viewModel(
        factory = HybridRiwayatViewModelFactory(repository)
    )



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(warnaKrem)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Daftar Reminder",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = warnaBiru
            )

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(5.dp))

                // ✅ Card Reminder Terdekat
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Reminder Terdekat",
                            color = warnaBiru,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        if (reminderTerdekat != null) {
                            val lansiaName = lansiaList
                                .filter { it.id in reminderTerdekat.lansiaIds }
                                .joinToString(", ") { it.nama }
                            val obatName = obatList
                                .filter { it.id in reminderTerdekat.obatIds }
                                .joinToString(", ") { it.nama }

                            Text(
                                text = lansiaName,
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                            Text(
                                text = obatName,
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )

                            Text(
                                text = "${reminderTerdekat.waktu} - ${reminderTerdekat.tanggal}",
                                color = Color(0xFF666666),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    reminderToDelete = reminderTerdekat.id
                                    showDeleteDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = Color.Red
                                    )
                                }

                                IconButton(onClick = {
                                    navController.navigate("detail_reminder/${reminderTerdekat.id}")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = warnaBiru
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Tidak ada reminder terdekat",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                            )
                        }
                    }
                }

                // ✅ List Semua Reminder (hanya reminder >= sekarang)
                filteredReminders.forEach { reminder ->
                    val lansiaName = lansiaList
                        .filter { it.id in reminder.lansiaIds }
                        .joinToString(", ") { it.nama }
                    val obatName = obatList
                        .filter { it.id in reminder.obatIds }
                        .joinToString(", ") { it.nama }

                    ReminderItem(
                        lansia = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Lansia : ")
                            }
                            append(lansiaName)
                        },
                        obat = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Obat : ")
                            }
                            append(obatName)
                        },
                        time = "${reminder.tanggal} - ${reminder.waktu}",
                        onClick = {
                            navController.navigate("detail_reminder/${reminder.id}")
                        },
                        onDelete = {
                            reminderToDelete = reminder.id
                            showDeleteDialog = true
                        },
                        onSelesai = {
                            val lansiaIdString = reminder.lansiaIds.joinToString(",")
                            val obatIdString = reminder.obatIds.joinToString(",")

                            val riwayat = Riwayat(
                                idRiwayat = UUID.randomUUID().toString(),
                                lansiaId = lansiaIdString,
                                obatId = if (obatIdString.isNotEmpty()) obatIdString else null,
                                kunjunganId = null,
                                jenis = "selesai di home",
                                keterangan = "Selesai via HomeScreen",
                                tanggal = reminder.tanggal,
                                waktu = reminder.waktu
                            )

                            riwayatViewModel.addRiwayat(riwayat) { success ->
                                if (success) {
                                    Log.d("HomeScreen", "Riwayat berhasil ditambahkan")
                                    reminderViewModel.deleteReminder(reminder.id) {}
                                } else {
                                    Log.e("HomeScreen", "Gagal menambahkan riwayat")
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        // ✅ Tombol Tambah Reminder
        AddButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp),
            onClick = {
                navController.navigate("add_reminder")
            }
        )

        // ✅ Dialog Konfirmasi Hapus
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    reminderToDelete = null
                },
                title = {
                    Text(
                        text = "Konfirmasi Hapus",
                        fontWeight = FontWeight.Bold,
                        color = BiruMuda.copy(alpha = 1.0f)
                    )
                },
                text = {
                    Text("Apakah Anda yakin ingin menghapus reminder ini?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            reminderToDelete?.let { id ->
                                reminderViewModel.deleteReminder(id) { success ->
                                    if (success) {
                                        Log.d("HomeScreen", "Reminder berhasil dihapus")
                                    } else {
                                        Log.e("HomeScreen", "Gagal menghapus reminder")
                                    }
                                }
                            }
                            showDeleteDialog = false
                            reminderToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            reminderToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = BiruMuda.copy(alpha = 1.0f))
                    ) {
                        Text("Tidak")
                    }
                }
            )
        }
    }
}

private fun simpanRiwayat(reminderId: String, lansiaList: List<Lansia>, obatList: List<Obat>,jenisRiwayat: String) {
    val db = FirebaseFirestore.getInstance()
    val riwayatId = db.collection("riwayat").document().id

    val riwayatData = hashMapOf(
        "idRiwayat" to riwayatId,
        "reminderId" to reminderId,
        "lansiaIds" to lansiaList.map { it.id }, // ✅ pakai id dari Lansia
        "obatIds" to obatList.map { it.id },     // ✅ pakai id dari Obat
        "waktuDiminum" to System.currentTimeMillis(),
        "status" to "SUDAH",
        "jenis" to jenisRiwayat
    )

    db.collection("riwayat")
        .document(riwayatId)
        .set(riwayatData)
        .addOnSuccessListener {
            Log.d("Riwayat", "Riwayat berhasil disimpan")
        }
        .addOnFailureListener { e ->
            Log.e("Riwayat", "Gagal menyimpan riwayat", e)
        }
}

@Composable
fun ReminderItem(
    lansia: AnnotatedString,
    obat: AnnotatedString,
    time: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onSelesai: () -> Unit
) {
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var sudahDiminum by rememberSaveable{ mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(text = lansia, fontSize = 20.sp, color = Color.Black)
            }
            Row {
                Text(text = obat, fontSize = 20.sp, color = Color.Black)
            }
            Row {
                Text(text = "Waktu : ", fontSize = 16.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                Text(text = time, fontSize = 16.sp, color = Color.DarkGray)
            }

//            Button(
//                onClick = onSelesai,
//                modifier = Modifier
//                    .padding(top = 8.dp)
//                    .fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
//            ) {
//                Text("Sudah Diminum", color = Color.White)
//            }

            Button(
                onClick = {
                    onSelesai()
                    sudahDiminum = true
                },
                enabled = !sudahDiminum,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (sudahDiminum) Color.Gray else Color(0xFF4CAF50)
                )
            ) {
                Text(
                    if(sudahDiminum) "Sudah Diminum ✓" else "Sudah Diminum",
                    color = Color.White
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = BiruTua.copy(alpha = 1.0f),
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Tambah",
            modifier = Modifier.size(30.dp)
        )
    }
}
