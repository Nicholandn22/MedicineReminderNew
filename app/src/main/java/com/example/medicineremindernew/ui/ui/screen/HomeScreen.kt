package com.example.medicineremindernew.ui.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medicineremindernew.R.drawable.add_file
import com.example.medicineremindernew.ui.ui.theme.OrenMuda


@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFC5007))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Navbar (judul) tetap di atas, tidak scroll
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

            // Konten reminder yang scrollable
            // Bisa menggunakan LazyColumn atau Column + verticalScroll

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState) // enable scroll vertikal
            ) {
                // CardView: Reminder Terdekat
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

                        Text(
                            text = "10:00 AM - 30 April 2024",
                            color = Color(0xFF666666),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { /* TODO: aksi selesai */ },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                            ) {
                                Text("SELESAI")
                            }

                            Button(
                                onClick = { /* TODO: aksi ubah */ },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text("UBAH")
                            }

                            Button(
                                onClick = { /* TODO: aksi hapus */ },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                            ) {
                                Text("HAPUS")
                            }
                        }
                    }
                }

                // List reminder (bisa dipakai list biasa karena sudah di dalam scrollable)
                ReminderItem(title = "Reminder 1", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 2", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 3", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 4", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 5", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 6", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 7", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 8", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 9", time = "23 May, 2024  02:00PM")
                ReminderItem(title = "Reminder 10", time = "23 May, 2024  02:00PM")
            }
        }

        // Tombol Add tetap floating di kanan bawah layar (di luar scroll)
        AddButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp),
            onClick = {
                navController.navigate("add_reminder")
            }
        )
    }
}

@Composable
fun ReminderItem(title: String, time: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            color = Color.Black
        )
        Row {
        Text(text = "Waktu : ",
        fontSize = 16.sp,
        color = Color.DarkGray
        )
        Text(
            text = time,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        }
    }
}

@Composable
fun AddButton(modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        onClick = { /* TODO: aksi tambah */ },
        modifier = modifier,
        containerColor = Color(0xFFFC5007),
        contentColor = Color.White,
        text = { Text("Add Reminder") },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah"
            )
        }
    )
}
@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = OrenMuda,         // Latar belakang tombol
        contentColor = Color.Black         // Warna konten (ikon & teks)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = add_file),  // pastikan file ada di drawable
                contentDescription = "Add",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Tambah Pengingat")
        }
    }
}


