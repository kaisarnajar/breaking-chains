package com.breakingchains.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_requests")
data class CallRequestEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val preferredDate: String,
    val preferredTime: String,
    val reasonNote: String,
    val status: String = "PENDING", // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val timestamp: Long = System.currentTimeMillis()
)
