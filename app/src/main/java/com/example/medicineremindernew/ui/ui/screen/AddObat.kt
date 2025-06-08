package com.example.medicineremindernew.ui.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.ui.theme.AbuMenu
import com.example.medicineremindernew.ui.ui.theme.OrenMuda


@Composable
fun AddObatScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // agar bisa scroll seluruh layar
    ) {
        // ✅ Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(OrenMuda), // pastikan warna terimport
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = back_white),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(text = "Tambah Obat", color = Color.White, fontSize = 20.sp)
        }

        // ✅ Subheader "Data Obat"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AbuMenu)
                .padding(16.dp)
        ) {
            Text("Data Obat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // ✅ Card Section: Nama, Jenis, Satuan, Notes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nama Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Nama Obat", modifier = Modifier.padding(bottom = 20.dp))

                Text("Jenis Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("Tablet", "Sirup", "Salep"),
                    selectedOption = remember { mutableStateOf("Tablet") }.value,
                ) { /* handle selection */ }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Satuan Dosis", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("mg", "ml", "IU"),
                    selectedOption = remember { mutableStateOf("mg") }.value,
                ) { /* handle selection */ }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Notes (Opsional)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("(Notes)", modifier = Modifier.padding(bottom = 20.dp))
            }
        }

        // ✅ Tombol Save & Clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onSaveClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = OrenMuda
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text("Save")
            }

            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = OrenMuda
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text("Clear")
            }
        }
    }
}
