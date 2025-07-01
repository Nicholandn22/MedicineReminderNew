package com.example.medicineremindernew.ui.ui.screen

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicineremindernew.R.drawable.add_file
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.repository.ReminderRepository
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.theme.Hijau
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModelFactory

@Composable
fun HomeScreen(navController: NavController, reminderViewModel: ReminderViewModel) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = remember { ObatDatabase.getDatabase(application) }

    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(ReminderRepository(db.reminderDao()))
    )

    val reminders by reminderViewModel.reminderList.collectAsState()
    val reminderTerdekat = reminders.sortedWith(compareBy({ it.tanggal }, { it.waktu })).firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3570F))
    ) {
        LaunchedEffect(reminders) {
            Log.d("ReminderDebug", "Reminder yang tampil: ${reminders.size}")
            reminders.forEach {
                Log.d("ReminderDebug", "Reminder -> Tanggal: ${it.tanggal}, Waktu: ${it.waktu}")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Home Page",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Reminder Terdekat",
                            color = Color(0xFFFF6600),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        if (reminderTerdekat != null) {
                            Text(
                                text = "${reminderTerdekat.waktu} - ${reminderTerdekat.tanggal}",
                                color = Color(0xFF666666),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                            )
                        } else {
                            Text(
                                text = "Tidak ada reminder terdekat",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (reminderTerdekat != null) {
                                OutlinedButton(
                                    onClick = { reminderViewModel.delete(reminderTerdekat) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(Color.Red))
                                ) {
                                    Text("HAPUS")
                                }

                                Spacer(modifier = Modifier.width(5.dp))

                                OutlinedButton(
                                    onClick = { navController.navigate("detail_reminder/${reminderTerdekat.id}") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrenMuda),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(OrenMuda))
                                ) {
                                    Text("UBAH")
                                }

                                Spacer(modifier = Modifier.width(5.dp))

                                OutlinedButton(
                                    onClick = { reminderViewModel.delete(reminderTerdekat) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Hijau),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(Hijau))
                                ) {
                                    Text("SELESAI")
                                }

                            }
                        }
                    }
                }

                reminders.forEach { reminder ->
                    ReminderItem(
                        title = "Reminder Lansia ${reminder.lansiaId} - Obat ${reminder.obatId}",
                        time = "${reminder.tanggal} - ${reminder.waktu}",
                        onClick = {
                            navController.navigate("detail_reminder/${reminder.id}")
                        },
                        onDelete = {
                            reminderViewModel.delete(reminder)
                        }
                    )
                }
            }
        }

        AddButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp),
            onClick = {
                navController.navigate("add_reminder")
            }
        )
    }
}

@Composable
fun ReminderItem(
    title: String,
    time: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
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
        // Bagian teks di kiri
        Column(
            modifier = Modifier.weight(1f) // agar teks fleksibel
        ) {
            Text(text = title, fontSize = 20.sp, color = Color.Black)
            Row {
                Text(text = "Waktu : ", fontSize = 16.sp, color = Color.DarkGray)
                Text(text = time, fontSize = 16.sp, color = Color.DarkGray)
            }
        }

        // Ikon hapus di kanan
        IconButton(
            onClick = onDelete
        ) {
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
        containerColor = OrenMuda,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Tambah",
            modifier = Modifier.size(30.dp)
        )
    }
}

