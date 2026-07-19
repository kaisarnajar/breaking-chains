package com.breakingchains.app.data.repository

import com.breakingchains.app.data.local.dao.RelapseLogDao
import com.breakingchains.app.data.local.dao.UserDao
import com.breakingchains.app.data.local.entity.toDomainModel
import com.breakingchains.app.data.model.Milestone
import com.breakingchains.app.data.model.MilestoneIcon
import com.breakingchains.app.data.model.RelapseLog
import com.breakingchains.app.data.model.toDomainModel
import com.breakingchains.app.data.model.toEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface TrackerRepository {
    fun getRelapseLogs(userId: String): Flow<List<RelapseLog>>
    suspend fun logRelapse(userId: String, trigger: String, note: String, moodBefore: String, severity: Int)
    fun getMilestones(activeStreakDays: Int): List<Milestone>
}

class TrackerRepositoryImpl(
    private val relapseLogDao: RelapseLogDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore? = null
) : TrackerRepository {

    // 1. Single Source of Truth: UI always observes Room DB
    override fun getRelapseLogs(userId: String): Flow<List<RelapseLog>> {
        return relapseLogDao.getLogsForUser(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun logRelapse(
        userId: String,
        trigger: String,
        note: String,
        moodBefore: String,
        severity: Int
    ) {
        val logId = "log_${System.currentTimeMillis()}"
        val timestamp = System.currentTimeMillis()

        val newLog = RelapseLog(
            id = logId,
            userId = userId,
            timestamp = timestamp,
            trigger = trigger,
            note = note,
            moodBefore = moodBefore,
            severity = severity
        )

        // 2. Offline-First: Write to Room DB immediately (Zero latency)
        relapseLogDao.insertLog(newLog.toEntity())

        // Reset user active streak days in Room DB
        val userEntity = userDao.getUserById(userId)
        if (userEntity != null) {
            userDao.updateUser(userEntity.copy(activeStreakDays = 0))
        }

        // 3. Fire-and-forget background sync to Firestore
        syncRelapseLogToFirestoreInBackground(newLog)
    }

    private fun syncRelapseLogToFirestoreInBackground(log: RelapseLog) {
        if (firestore == null) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val logMap = mapOf(
                    "id" to log.id,
                    "userId" to log.userId,
                    "timestamp" to log.timestamp,
                    "trigger" to log.trigger,
                    "note" to log.note,
                    "moodBefore" to log.moodBefore,
                    "severity" to log.severity
                )
                firestore.collection("relapse_logs").document(log.id).set(logMap)

                // Also update user streak in Firestore
                firestore.collection("users").document(log.userId).update("activeStreakDays", 0)
            } catch (e: Exception) {
                // Fail quietly if offline; Room DB holds local data
            }
        }
    }

    override fun getMilestones(activeStreakDays: Int): List<Milestone> {
        return listOf(
            Milestone(
                title = "3 Days",
                subtitle = "Fresh Start",
                requiredDays = 3,
                isUnlocked = activeStreakDays >= 3,
                iconType = MilestoneIcon.MEDAL
            ),
            Milestone(
                title = "7 Days",
                subtitle = "Momentum",
                requiredDays = 7,
                isUnlocked = activeStreakDays >= 7,
                iconType = MilestoneIcon.RIBBON
            ),
            Milestone(
                title = "30 Days",
                subtitle = "Persistence",
                requiredDays = 30,
                isUnlocked = activeStreakDays >= 30,
                iconType = MilestoneIcon.LOCK
            ),
            Milestone(
                title = "90 Days",
                subtitle = "Lifestyle",
                requiredDays = 90,
                isUnlocked = activeStreakDays >= 90,
                iconType = MilestoneIcon.LOCK
            )
        )
    }
}
