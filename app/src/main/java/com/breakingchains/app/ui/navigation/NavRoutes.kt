package com.breakingchains.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object UserTracker : Screen("tracker", "Tracker")
    object LogRelapse : Screen("log_relapse", "Log Relapse")
    object ScheduleCall : Screen("schedule_call", "Schedule Call")
    object AdminDashboard : Screen("admin_dashboard", "Admin Dashboard")
}
