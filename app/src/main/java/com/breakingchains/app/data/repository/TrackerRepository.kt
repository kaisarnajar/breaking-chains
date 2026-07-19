package com.breakingchains.app.data.repository

import com.breakingchains.app.data.local.dao.RelapseLogDao
import com.breakingchains.app.data.local.dao.UserDao
import com.breakingchains.app.data.local.entity.RelapseLogEntity
import com.breakingchains.app.data.local.entity.toDomainModel
import com.breakingchains.app.data.model.Milestone
import com.breakingchains.app.data.model.MilestoneIcon
import com.breakingchains.app.data.model.RelapseLog
import com.breakingchains.app.data.model.toDomainModel
import com.breakingchains.app.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TrackerRepository {
    fun getRelapseLogs(userId: String): Flow<List<RelapseLog>>
    suspend fun logRelapse(userId: String, trigger: String, note: String, moodBefore: String, severity: Int)
    fun getMilestones(activeStreakDays: Int): List<Milestone>
}

class TrackerRepositoryImpl(
    private val relapseLogDao: RelapseLogDao,
    private val userDao: UserDao
) : TrackerRepository {

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
        val newLog = RelapseLog(
            id = "log_${System.currentTimeMillis()}",
            userId = userId,
            timestamp = System.currentTimeMillis(),
            trigger = trigger,
            note = note,
            moodBefore = moodBefore,
            severity = severity
        )
        relapseLogDao.insertLog(newLog.toEntity())

        // Reset user active streak days in Room DB upon relapse
        val userEntity = userDao.getUserById(userId)
        if (userEntity != null) {
            userDao.updateUser(userEntity.copy(activeStreakDays = 0))
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
