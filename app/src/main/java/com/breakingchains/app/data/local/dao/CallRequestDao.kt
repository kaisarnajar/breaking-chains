package com.breakingchains.app.data.local.dao

import androidx.room.*
import com.breakingchains.app.data.local.entity.CallRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallRequestDao {
    @Query("SELECT * FROM call_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getCallRequestsForUser(userId: String): Flow<List<CallRequestEntity>>

    @Query("SELECT * FROM call_requests ORDER BY timestamp DESC")
    fun getAllCallRequests(): Flow<List<CallRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRequest(request: CallRequestEntity)

    @Query("UPDATE call_requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: String, status: String)
}
