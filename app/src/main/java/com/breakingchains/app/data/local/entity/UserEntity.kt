package com.breakingchains.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.breakingchains.app.data.model.User
import com.breakingchains.app.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val joinedDate: String = "October 2023",
    val activeStreakDays: Int = 0
)

fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = role,
        avatarUrl = avatarUrl,
        joinedDate = joinedDate,
        activeStreakDays = activeStreakDays
    )
}

fun User.toEntity(passwordHash: String): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        passwordHash = passwordHash,
        role = role,
        avatarUrl = avatarUrl,
        joinedDate = joinedDate,
        activeStreakDays = activeStreakDays
    )
}
