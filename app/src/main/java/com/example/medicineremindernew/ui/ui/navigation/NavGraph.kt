package com.example.medicineremindernew.ui.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicineremindernew.ui.ui.screen.AddReminderScreen
import com.example.medicineremindernew.ui.ui.screen.HomeScreen
import com.example.medicineremindernew.ui.ui.screen.LansiaScreen
import com.example.medicineremindernew.ui.ui.screen.ObatScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable("add_reminder") {
            AddReminderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
//        composable(BottomNavItem.Home.route) {
//            HomeScreen(navController = navController)
//        }

        composable(BottomNavItem.Lansia.route) { LansiaScreen() }
        composable(BottomNavItem.Obat.route) { ObatScreen() }
    }
}
