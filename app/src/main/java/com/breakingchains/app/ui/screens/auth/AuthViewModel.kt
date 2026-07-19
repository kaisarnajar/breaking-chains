package com.breakingchains.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breakingchains.app.data.model.AuthResult
import com.breakingchains.app.data.model.UserRole
import com.breakingchains.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val selectedRole: UserRole = UserRole.USER,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterUiState(
    val nameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val selectedRole: UserRole = UserRole.USER,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ForgotPasswordUiState(
    val emailInput: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()

    // Login actions
    fun onLoginEmailChanged(email: String) {
        _loginState.update { it.copy(emailInput = email, errorMessage = null) }
    }

    fun onLoginPasswordChanged(password: String) {
        _loginState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun onLoginRoleSelected(role: UserRole) {
        _loginState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    fun toggleLoginPasswordVisibility() {
        _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login(onSuccess: (UserRole) -> Unit) {
        val currentState = _loginState.value
        _loginState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(
                email = currentState.emailInput,
                password = currentState.passwordInput,
                role = currentState.selectedRole
            )
            when (result) {
                is AuthResult.Success -> {
                    _loginState.update { it.copy(isLoading = false) }
                    onSuccess(result.user.role)
                }
                is AuthResult.Error -> {
                    _loginState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    // Register actions
    fun onRegisterNameChanged(name: String) {
        _registerState.update { it.copy(nameInput = name, errorMessage = null) }
    }

    fun onRegisterEmailChanged(email: String) {
        _registerState.update { it.copy(emailInput = email, errorMessage = null) }
    }

    fun onRegisterPasswordChanged(password: String) {
        _registerState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun onRegisterConfirmPasswordChanged(password: String) {
        _registerState.update { it.copy(confirmPasswordInput = password, errorMessage = null) }
    }

    fun onRegisterRoleSelected(role: UserRole) {
        _registerState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    fun toggleRegisterPasswordVisibility() {
        _registerState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun register(onSuccess: (UserRole) -> Unit) {
        val currentState = _registerState.value

        if (currentState.passwordInput != currentState.confirmPasswordInput) {
            _registerState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        _registerState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.register(
                name = currentState.nameInput,
                email = currentState.emailInput,
                password = currentState.passwordInput,
                role = currentState.selectedRole
            )
            when (result) {
                is AuthResult.Success -> {
                    _registerState.update { it.copy(isLoading = false) }
                    onSuccess(result.user.role)
                }
                is AuthResult.Error -> {
                    _registerState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    // Forgot Password actions
    fun onForgotPasswordEmailChanged(email: String) {
        _forgotPasswordState.update { it.copy(emailInput = email, errorMessage = null, successMessage = null) }
    }

    fun resetPassword() {
        val currentState = _forgotPasswordState.value
        _forgotPasswordState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = authRepository.resetPassword(currentState.emailInput)
            result.onSuccess { msg ->
                _forgotPasswordState.update { it.copy(isLoading = false, successMessage = msg) }
            }.onFailure { err ->
                _forgotPasswordState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
