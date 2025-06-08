package com.example.medicineremindernew.ui.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicineremindernew.ui.HomeScreen
import com.example.medicineremindernew.ui.ui.screen.ProfileScreen
import com.example.medicineremindernew.ui.ui.screen.ReminderScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Reminder.route) { ReminderScreen() }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }
    }
}