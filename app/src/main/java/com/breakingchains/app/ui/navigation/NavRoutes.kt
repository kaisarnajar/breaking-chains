package com.breakingchains.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")
    object ForgotPassword : Screen("forgot_password", "Forgot Password")
    object UserTracker : Screen("tracker", "Tracker")
    object RelapseLog : Screen("log_relapse", "Log Relapse")
    object ScheduleCall : Screen("schedule_call", "Schedule Call")
    object AdminDashboard : Screen("admin_dashboard", "Admin Dashboard")
}
