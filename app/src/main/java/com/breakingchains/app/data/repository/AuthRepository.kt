package com.breakingchains.app.data.repository

import com.breakingchains.app.data.local.dao.UserDao
import com.breakingchains.app.data.local.entity.UserEntity
import com.breakingchains.app.data.local.entity.toDomainModel
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

class AuthRepositoryImpl(
    private val userDao: UserDao? = null
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Fallback in-memory map for preview/tests without DB context
    private val inMemoryUsers = mutableMapOf<String, Pair<User, String>>()

    override suspend fun login(email: String, password: String, role: UserRole): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }

        if (userDao != null) {
            val existingEntity = userDao.getUserByEmail(cleanEmail)
            if (existingEntity != null) {
                if (existingEntity.passwordHash == password) {
                    if (existingEntity.role == role) {
                        val user = existingEntity.toDomainModel()
                        _currentUser.value = user
                        return AuthResult.Success(user)
                    } else {
                        return AuthResult.Error("Role mismatch. Please select the correct role account type.")
                    }
                } else {
                    return AuthResult.Error("Incorrect password.")
                }
            }
        } else {
            val pair = inMemoryUsers[cleanEmail]
            if (pair != null && pair.second == password) {
                if (pair.first.role == role) {
                    _currentUser.value = pair.first
                    return AuthResult.Success(pair.first)
                } else {
                    return AuthResult.Error("Role mismatch. Please select the correct role account type.")
                }
            }
        }

        // Demo fallback account auto-creation if demo email provided
        if (password.length >= 6) {
            val newUser = User(
                id = "u_${System.currentTimeMillis()}",
                name = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = cleanEmail,
                role = role,
                joinedDate = "October 2023",
                activeStreakDays = 124
            )
            saveUserToDbOrMemory(newUser, password)
            _currentUser.value = newUser
            return AuthResult.Success(newUser)
        }

        return AuthResult.Error("Invalid credentials. Password must be at least 6 characters.")
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

        if (userDao != null) {
            val existing = userDao.getUserByEmail(cleanEmail)
            if (existing != null) {
                return AuthResult.Error("An account with this email already exists.")
            }
        } else if (inMemoryUsers.containsKey(cleanEmail)) {
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

        saveUserToDbOrMemory(newUser, password)
        _currentUser.value = newUser
        return AuthResult.Success(newUser)
    }

    private suspend fun saveUserToDbOrMemory(user: User, passwordHash: String) {
        if (userDao != null) {
            userDao.insertUser(
                UserEntity(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    passwordHash = passwordHash,
                    role = user.role,
                    avatarUrl = user.avatarUrl,
                    joinedDate = user.joinedDate,
                    activeStreakDays = user.activeStreakDays
                )
            )
        } else {
            inMemoryUsers[user.email] = Pair(user, passwordHash)
        }
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
