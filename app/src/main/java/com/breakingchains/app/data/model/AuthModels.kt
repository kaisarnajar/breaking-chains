package com.breakingchains.app.data.model

enum class UserRole {
    USER,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val joinedDate: String = "October 2023",
    val activeStreakDays: Int = 0
)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
