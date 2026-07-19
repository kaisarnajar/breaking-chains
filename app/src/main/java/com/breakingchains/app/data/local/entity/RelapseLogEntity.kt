package com.breakingchains.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relapse_logs")
data class RelapseLogEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val timestamp: Long,
    val trigger: String,
    val note: String,
    val moodBefore: String = "Stressed",
    val severity: Int = 1
)
