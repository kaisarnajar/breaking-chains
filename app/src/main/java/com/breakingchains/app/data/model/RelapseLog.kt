package com.breakingchains.app.data.model

import com.breakingchains.app.data.local.entity.RelapseLogEntity

data class RelapseLog(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val trigger: String,
    val note: String,
    val moodBefore: String = "Stressed",
    val severity: Int = 1
)

fun RelapseLogEntity.toDomainModel(): RelapseLog {
    return RelapseLog(
        id = id,
        userId = userId,
        timestamp = timestamp,
        trigger = trigger,
        note = note,
        moodBefore = moodBefore,
        severity = severity
    )
}

fun RelapseLog.toEntity(): RelapseLogEntity {
    return RelapseLogEntity(
        id = id,
        userId = userId,
        timestamp = timestamp,
        trigger = trigger,
        note = note,
        moodBefore = moodBefore,
        severity = severity
    )
}

data class Milestone(
    val title: String,
    val subtitle: String,
    val requiredDays: Int,
    val isUnlocked: Boolean,
    val iconType: MilestoneIcon
)

enum class MilestoneIcon {
    MEDAL,
    RIBBON,
    LOCK
}
