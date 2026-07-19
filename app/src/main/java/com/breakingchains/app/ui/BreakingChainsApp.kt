package com.breakingchains.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.breakingchains.app.data.local.AppDatabase
import com.breakingchains.app.data.model.UserRole
import com.breakingchains.app.data.repository.AuthRepositoryImpl
import com.breakingchains.app.data.repository.TrackerRepositoryImpl
import com.breakingchains.app.ui.navigation.Screen
import com.breakingchains.app.ui.screens.admin.AdminDashboardScreen
import com.breakingchains.app.ui.screens.auth.AuthViewModel
import com.breakingchains.app.ui.screens.auth.ForgotPasswordScreen
import com.breakingchains.app.ui.screens.auth.LoginScreen
import com.breakingchains.app.ui.screens.auth.RegisterScreen
import com.breakingchains.app.ui.screens.relapse.RelapseLogScreen
import com.breakingchains.app.ui.screens.relapse.RelapseLogViewModel
import com.breakingchains.app.ui.screens.schedule.ScheduleCallScreen
import com.breakingchains.app.ui.screens.tracker.TrackerViewModel
import com.breakingchains.app.ui.screens.tracker.UserTrackerScreen
import com.breakingchains.app.ui.theme.BreakingChainsTheme

@Composable
fun BreakingChainsApp() {
    BreakingChainsTheme {
        val context = LocalContext.current
        val navController = rememberNavController()

        // Room Database & Persistent Repositories
        val database = remember { AppDatabase.getInstance(context) }
        val authRepository = remember { AuthRepositoryImpl(database.userDao()) }
        val trackerRepository = remember { TrackerRepositoryImpl(database.relapseLogDao(), database.userDao()) }

        val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }
        val trackerViewModel: TrackerViewModel = viewModel { TrackerViewModel(authRepository, trackerRepository) }
        val relapseLogViewModel: RelapseLogViewModel = viewModel { RelapseLogViewModel(authRepository, trackerRepository) }

        val currentUser by authViewModel.currentUser.collectAsState()
        val loginState by authViewModel.loginState.collectAsState()
        val registerState by authViewModel.registerState.collectAsState()
        val forgotPasswordState by authViewModel.forgotPasswordState.collectAsState()
        val trackerUiState by trackerViewModel.uiState.collectAsState()
        val relapseLogUiState by relapseLogViewModel.uiState.collectAsState()

        val startDestination = if (currentUser != null) {
            if (currentUser?.role == UserRole.ADMIN) Screen.AdminDashboard.route else Screen.UserTracker.route
        } else {
            Screen.Login.route
        }

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // Auth Screens
            composable(Screen.Login.route) {
                LoginScreen(
                    state = loginState,
                    onEmailChanged = authViewModel::onLoginEmailChanged,
                    onPasswordChanged = authViewModel::onLoginPasswordChanged,
                    onTogglePasswordVisibility = authViewModel::toggleLoginPasswordVisibility,
                    onLoginClick = {
                        authViewModel.login { role ->
                            val targetRoute = if (role == UserRole.ADMIN) Screen.AdminDashboard.route else Screen.UserTracker.route
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    state = registerState,
                    onNameChanged = authViewModel::onRegisterNameChanged,
                    onEmailChanged = authViewModel::onRegisterEmailChanged,
                    onPasswordChanged = authViewModel::onRegisterPasswordChanged,
                    onConfirmPasswordChanged = authViewModel::onRegisterConfirmPasswordChanged,
                    onTogglePasswordVisibility = authViewModel::toggleRegisterPasswordVisibility,
                    onRegisterClick = {
                        authViewModel.register { role ->
                            val targetRoute = if (role == UserRole.ADMIN) Screen.AdminDashboard.route else Screen.UserTracker.route
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    state = forgotPasswordState,
                    onEmailChanged = authViewModel::onForgotPasswordEmailChanged,
                    onResetPasswordClick = authViewModel::resetPassword,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Core App Screens
            composable(Screen.UserTracker.route) {
                UserTrackerScreen(
                    state = trackerUiState,
                    onPreviousMonth = trackerViewModel::onPreviousMonth,
                    onNextMonth = trackerViewModel::onNextMonth,
                    onNavigateToLogRelapse = { navController.navigate(Screen.RelapseLog.route) },
                    onNavigateToScheduleCall = { navController.navigate(Screen.ScheduleCall.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) }
                )
            }

            composable(Screen.RelapseLog.route) {
                RelapseLogScreen(
                    state = relapseLogUiState,
                    onTriggerSelected = relapseLogViewModel::onTriggerSelected,
                    onMoodSelected = relapseLogViewModel::onMoodSelected,
                    onNoteChanged = relapseLogViewModel::onNoteChanged,
                    onSubmitClick = {
                        relapseLogViewModel.submitRelapseLog {
                            navController.popBackStack()
                        }
                    },
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
                    onNavigateBack = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
