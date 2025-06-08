package com.example.medicineremindernew.ui.ui.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Reminder : BottomNavItem("reminder", "Reminder", Icons.Default.Notifications)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}