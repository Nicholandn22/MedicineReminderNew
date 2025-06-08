package com.example.medicineremindernew.ui.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.R.drawable.add_file
import com.example.medicineremindernew.R.drawable.plus_black
import com.example.medicineremindernew.ui.ui.theme.AbuMenu
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.theme.PutihKolom


@Composable
fun AddReminderScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val pengulanganOptions = listOf("Harian", "Mingguan", "Bulanan")
    val nadaDeringOptions = listOf("Nada 1", "Nada 2", "Nada 3")

    var selectedPengulangan by remember { mutableStateOf(pengulanganOptions.first()) }
    var selectedNadaDering by remember { mutableStateOf(nadaDeringOptions.first()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(
                    painter = painterResource(id = add_file),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp).size(32.dp)
                )
            }
            Text("Tambah Pengingat", color = Color.White, fontSize = 20.sp)
        }

        // Section: Pengingat
        SectionTitle("Pengingat")

        CardSection {
            TextLabelValue(label = "Tanggal", value = "DD/MM/YYYY")
            TextLabelValue(label = "Waktu", value = "(Pilih Waktu)")

            Text(text = "Pengulangan", fontWeight = FontWeight.Bold)
            DropdownMenuField(options = pengulanganOptions, selectedOption = selectedPengulangan) {
                selectedPengulangan = it
            }

            Text(text = "Nada Dering", fontWeight = FontWeight.Bold)
            DropdownMenuField(options = nadaDeringOptions, selectedOption = selectedNadaDering) {
                selectedNadaDering = it
            }
        }

        // Section: Pasien
        SectionWithAddButton("Pasien")

        CardSection {
            ReminderButton(text = "Data Lansia 1")
            ReminderButton(text = "Data Lansia 2")
        }

        // Section: Obat
        SectionWithAddButton("List Obat")

        CardSection {
            ReminderButton(text = "Obat 1")
            ReminderButton(text = "Obat 2")
        }

        // Save & Cancel buttons
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
                )
                ,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text("Save")
            }

            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = OrenMuda
                )
                ,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}

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
fun SectionWithAddButton(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AbuMenu)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = plus_black),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CardSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun TextLabelValue(label: String, value: String) {
    Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    Text(text = value, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))
}

@Composable
fun DropdownMenuField(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
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
                )}

            }
        }
    }


@Composable
fun ReminderButton(text: String) {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(PutihKolom
        )
    ) {
        Text(text)
    }
}
