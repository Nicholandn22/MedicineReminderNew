package com.example.medicineremindernew.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.medicineremindernew.ui.ui.components.BottomNavigationBar
import com.example.medicineremindernew.ui.ui.navigation.NavGraph
import com.example.medicineremindernew.ui.ui.theme.MedicineReminderNewTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicineReminderNewTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) {
                    NavGraph(navController = navController)
                }
            }
            }
        }
    }


@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title: Home Page
        Text(
            text = "Home Page",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 20.dp),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // CardView: Reminder Terdekat
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0)) // Warna latar mirip drawable corner.xml
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

        // Reminder 1 - 6
        ReminderItem(title = "Reminder 1", time = "23 May, 2024  02:00PM")
        ReminderItem(title = "Reminder 2", time = "23 May, 2024  02:00PM")
        ReminderItem(title = "Reminder 3", time = "23 May, 2024  02:00PM")
        ReminderItem(title = "Reminder 4", time = "23 May, 2024  02:00PM")
        ReminderItem(title = "Reminder 5", time = "23 May, 2024  02:00PM")
        ReminderItem(title = "Reminder 6", time = "23 May, 2024  02:00PM")
    }
}

@Composable
fun ReminderItem(title: String, time: String) {
    Text(
        text = title,
        fontSize = 25.sp,
        modifier = Modifier.padding(top = 12.dp)
    )
    Text(
        text = time,
        fontSize = 20.sp,
        color = Color.DarkGray
    )
}
