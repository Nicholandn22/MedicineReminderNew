package com.example.medicineremindernew.ui.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ObatScreen() {
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
                text = "Obat Page",
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


                // List reminder (bisa dipakai list biasa karena sudah di dalam scrollable)
                ObatItem(name = "Lansia 1", jenis = "Tablet")
                ObatItem(name = "Lansia 2", jenis = "Sirup")
                ObatItem(name = "Lansia 3", jenis = "Krim")
                ObatItem(name = "Lansia 4", jenis = "Puyer")
                ObatItem(name = "Lansia 5", jenis = "Tablet")
                ObatItem(name = "Lansia 6", jenis = "Krim")


            }
        }

        // Tombol Add tetap floating di kanan bawah layar (di luar scroll)
        AddObat(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp)
        )
    }
}

@Composable
fun ObatItem(name: String,jenis: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Text(
            text = name,
            fontSize = 20.sp,
            color = Color.Black
        )
        Row {

            Text(
                text = "Jenis Obat : ",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = jenis,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }

    }
}

@Composable
fun AddObat(modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        onClick = { /* TODO: aksi tambah */ },
        modifier = modifier,
        containerColor = Color(0xFFFC5007),
        contentColor = Color.White,
        text = { Text("Add Obat") },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah"
            )
        }
    )
}