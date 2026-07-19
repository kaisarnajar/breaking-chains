package com.breakingchains.app.data.repository

import com.breakingchains.app.BuildConfig
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
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(name: String, email: String, password: String): AuthResult
    suspend fun resetPassword(email: String): Result<String>
    fun logout()
}

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private fun isAdminEmail(email: String): Boolean {
        val adminList = BuildConfig.ADMIN_EMAILS.split(",").map { it.trim().lowercase() }
        return adminList.contains(email.trim().lowercase())
    }

    override suspend fun login(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }

        val existingEntity = userDao.getUserByEmail(cleanEmail)
        if (existingEntity != null) {
            if (existingEntity.passwordHash == password) {
                val expectedRole = if (isAdminEmail(cleanEmail)) UserRole.ADMIN else existingEntity.role
                val updatedUser = if (existingEntity.role != expectedRole) {
                    val newEntity = existingEntity.copy(role = expectedRole)
                    userDao.updateUser(newEntity)
                    newEntity.toDomainModel()
                } else {
                    existingEntity.toDomainModel()
                }
                _currentUser.value = updatedUser
                return AuthResult.Success(updatedUser)
            } else {
                return AuthResult.Error("Incorrect password. Please try again.")
            }
        }

        // Quick onboarding / demo account auto-creation if valid credentials provided
        if (password.length >= 6) {
            val role = if (isAdminEmail(cleanEmail)) UserRole.ADMIN else UserRole.USER
            val newUserEntity = UserEntity(
                id = "u_${System.currentTimeMillis()}",
                name = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = cleanEmail,
                passwordHash = password,
                role = role,
                joinedDate = "October 2023",
                activeStreakDays = if (role == UserRole.ADMIN) 365 else 1
            )
            userDao.insertUser(newUserEntity)
            val user = newUserEntity.toDomainModel()
            _currentUser.value = user
            return AuthResult.Success(user)
        }

        return AuthResult.Error("Account not found. Please register first.")
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
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

        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return AuthResult.Error("An account with this email already exists. Please log in.")
        }

        val role = if (isAdminEmail(cleanEmail)) UserRole.ADMIN else UserRole.USER
        val newUserEntity = UserEntity(
            id = "u_${System.currentTimeMillis()}",
            name = name.trim(),
            email = cleanEmail,
            passwordHash = password,
            role = role,
            joinedDate = "Today",
            activeStreakDays = 0
        )

        userDao.insertUser(newUserEntity)
        val user = newUserEntity.toDomainModel()
        _currentUser.value = user
        return AuthResult.Success(user)
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
