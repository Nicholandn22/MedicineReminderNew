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
fun LansiaScreen() {
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
                text = "Lansia Page",
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
                LansiaItem(name = "Lansia 1",age = 23 , GolDar = "AB")
                LansiaItem(name = "Lansia 2",age = 24 , GolDar = "B")
                LansiaItem(name = "Lansia 3",age = 43 , GolDar = "C")
                LansiaItem(name = "Lansia 4",age = 63 , GolDar = "O")
                LansiaItem(name = "Lansia 5",age = 28 , GolDar = "O+")
                LansiaItem(name = "Lansia 6",age = 13 , GolDar = "AD")

            }
        }

        // Tombol Add tetap floating di kanan bawah layar (di luar scroll)
        AddLansia(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp)
        )
    }
}

@Composable
fun LansiaItem(name: String, age: Int, GolDar: String) {
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
            text = "Usia : ",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            text = age.toString() + " ",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            text = "| Golongan Darah : ",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            text = GolDar,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        }

    }
}

@Composable
fun AddLansia(modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        onClick = { /* TODO: aksi tambah */ },
        modifier = modifier,
        containerColor = Color(0xFFFC5007),
        contentColor = Color.White,
        text = { Text("Add Lansia") },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah"
            )
        }
    )
}