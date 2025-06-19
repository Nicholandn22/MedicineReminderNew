package com.example.medicineremindernew.ui.ui.screen

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

// GANTI 'viewModel.reminde' DENGAN 'reminderViewModel.reminderList'
    val reminders by reminderViewModel.reminderList.collectAsState()

    val reminderTerdekat = reminders
        .sortedWith(compareBy({ it.tanggal }, { it.waktu }))
        .firstOrNull()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFC5007))
    ) {
        LaunchedEffect(reminders) {
            Log.d("ReminderDebug", "Reminder yang tampil: ${reminders.size}")
            reminders.forEach {
                Log.d("ReminderDebug", "Reminder -> Tanggal: ${it.tanggal}, Waktu: ${it.waktu}")
            }
//            Log.d("ReminderDebug", "Semua data: ${Reminder.tanggal} ${Reminder.waktu}")

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Navbar
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
                // Reminder Terdekat (sementara contoh statis, bisa diubah jadi reminder[0])
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
                            .background(Color(0xFFFFF3E0))
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
                                Button(onClick = {
                                    reminderViewModel.delete(reminderTerdekat)
                                }) {
                                    Text("HAPUS")
                                }

                                Button(onClick = {
                                    navController.navigate("detail_reminder/${reminderTerdekat.id}")
                                }) {
                                    Text("UBAH")
                                }

                                Button(onClick = {
                                    // Tambahkan aksi "SELESAI", misal menghapus atau memberi status
                                    reminderViewModel.delete(reminderTerdekat)
                                }) {
                                    Text("SELESAI")
                                }
                            }

                        }
                    }
                }

                // Daftar reminder dari database
                reminders.forEach { reminder ->
                    ReminderItem(
                        title = "Reminder Lansia ${reminder.lansiaId} - Obat ${reminder.obatId}",
                        time = "${reminder.tanggal} - ${reminder.waktu}",
                        onClick = {
                            navController.navigate("detail_reminder/${reminder.id}")
                        },
                        onDelete = {
                            reminderViewModel.delete(reminder) // 🔸 ini yang menghapus data
                        }
                    )
                }
            }
        }

        // Tombol tambah reminder
        AddButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 120.dp),
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
    onDelete: () -> Unit // Tambahan parameter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = title, fontSize = 20.sp, color = Color.Black)
        Row {
            Text(text = "Waktu : ", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = time, fontSize = 16.sp, color = Color.DarkGray)
        }

        // 🔸 Ini bagian tombol hapus
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onDelete,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Hapus")
            }
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
        contentColor = Color.Black
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = add_file),
                contentDescription = "Add",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Tambah Pengingat")
        }
    }
}
