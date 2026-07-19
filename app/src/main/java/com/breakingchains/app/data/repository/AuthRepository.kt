package com.breakingchains.app.data.repository

import com.breakingchains.app.data.model.AuthResult
import com.breakingchains.app.data.model.User
import com.breakingchains.app.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(email: String, password: String, role: UserRole): AuthResult
    suspend fun register(name: String, email: String, password: String, role: UserRole): AuthResult
    suspend fun resetPassword(email: String): Result<String>
    fun logout()
}

class AuthRepositoryImpl : AuthRepository {

    // Mock in-memory user registry for initial development
    private val registeredUsers = mutableMapOf<String, Pair<User, String>>(
        "user@example.com" to Pair(
            User(
                id = "u_101",
                name = "Elena Richardson",
                email = "user@example.com",
                role = UserRole.USER,
                joinedDate = "October 12, 2023",
                activeStreakDays = 124
            ),
            "password123"
        ),
        "admin@example.com" to Pair(
            User(
                id = "a_999",
                name = "Serenity Mentor Admin",
                email = "admin@example.com",
                role = UserRole.ADMIN,
                joinedDate = "January 1, 2023",
                activeStreakDays = 365
            ),
            "admin123"
        )
    )

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String, role: UserRole): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val userPair = registeredUsers[cleanEmail]

        return if (userPair != null && userPair.second == password) {
            val user = userPair.first
            if (user.role == role) {
                _currentUser.value = user
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Role mismatch. Please select the correct role account type.")
            }
        } else if (cleanEmail.isNotEmpty() && password.length >= 6) {
            // Auto-create/demo login fallback for easy testing
            val newUser = User(
                id = "u_${System.currentTimeMillis()}",
                name = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = cleanEmail,
                role = role,
                joinedDate = "October 2023",
                activeStreakDays = 1
            )
            registeredUsers[cleanEmail] = Pair(newUser, password)
            _currentUser.value = newUser
            AuthResult.Success(newUser)
        } else {
            AuthResult.Error("Invalid email or password. Password must be at least 6 characters.")
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }
        if (name.isBlank()) {
            return AuthResult.Error("Full Name cannot be empty.")
        }

        if (registeredUsers.containsKey(cleanEmail)) {
            return AuthResult.Error("An account with this email already exists.")
        }

        val newUser = User(
            id = "u_${System.currentTimeMillis()}",
            name = name.trim(),
            email = cleanEmail,
            role = role,
            joinedDate = "Today",
            activeStreakDays = 0
        )
        registeredUsers[cleanEmail] = Pair(newUser, password)
        _currentUser.value = newUser
        return AuthResult.Success(newUser)
    }

    override suspend fun resetPassword(email: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        return if (cleanEmail.isNotEmpty() && cleanEmail.contains("@")) {
            Result.success("Password reset instructions have been sent to $cleanEmail")
        } else {
            Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
    }

    override fun logout() {
        _currentUser.value = null
    }
}
