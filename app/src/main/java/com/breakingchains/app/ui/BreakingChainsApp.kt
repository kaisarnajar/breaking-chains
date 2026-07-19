package com.breakingchains.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.breakingchains.app.ui.navigation.Screen
import com.breakingchains.app.ui.screens.admin.AdminDashboardScreen
import com.breakingchains.app.ui.screens.relapse.RelapseLogScreen
import com.breakingchains.app.ui.screens.schedule.ScheduleCallScreen
import com.breakingchains.app.ui.screens.tracker.UserTrackerScreen
import com.breakingchains.app.ui.theme.BreakingChainsTheme

@Composable
fun BreakingChainsApp() {
    BreakingChainsTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.UserTracker.route
        ) {
            composable(Screen.UserTracker.route) {
                UserTrackerScreen(
                    onNavigateToLogRelapse = { navController.navigate(Screen.LogRelapse.route) },
                    onNavigateToScheduleCall = { navController.navigate(Screen.ScheduleCall.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) }
                )
            }

            composable(Screen.LogRelapse.route) {
                RelapseLogScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ScheduleCall.route) {
                ScheduleCallScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
