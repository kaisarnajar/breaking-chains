package com.breakingchains.app.data.local

import androidx.room.TypeConverter
import com.breakingchains.app.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(roleStr: String): UserRole {
        return try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.USER
        }
    }
}
