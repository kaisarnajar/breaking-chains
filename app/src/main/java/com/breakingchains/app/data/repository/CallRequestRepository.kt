package com.breakingchains.app.data.repository

import com.breakingchains.app.data.local.dao.CallRequestDao
import com.breakingchains.app.data.local.entity.CallRequestEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

data class CallRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val preferredDate: String,
    val preferredTime: String,
    val reasonNote: String,
    val status: String = "PENDING",
    val timestamp: Long = System.currentTimeMillis()
)

fun CallRequestEntity.toDomainModel(): CallRequest {
    return CallRequest(
        id = id,
        userId = userId,
        userName = userName,
        userEmail = userEmail,
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        reasonNote = reasonNote,
        status = status,
        timestamp = timestamp
    )
}

fun CallRequest.toEntity(): CallRequestEntity {
    return CallRequestEntity(
        id = id,
        userId = userId,
        userName = userName,
        userEmail = userEmail,
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        reasonNote = reasonNote,
        status = status,
        timestamp = timestamp
    )
}

interface CallRequestRepository {
    fun getCallRequestsForUser(userId: String): Flow<List<CallRequestEntity>>
    fun getAllCallRequests(): Flow<List<CallRequestEntity>>
    suspend fun scheduleCall(userId: String, userName: String, userEmail: String, preferredDate: String, preferredTime: String, note: String)
    suspend fun updateRequestStatus(requestId: String, status: String)
}

class CallRequestRepositoryImpl(
    private val callRequestDao: CallRequestDao,
    private val firestore: FirebaseFirestore? = null
) : CallRequestRepository {

    override fun getCallRequestsForUser(userId: String): Flow<List<CallRequestEntity>> {
        return callRequestDao.getCallRequestsForUser(userId)
    }

    override fun getAllCallRequests(): Flow<List<CallRequestEntity>> {
        return callRequestDao.getAllCallRequests()
    }

    override suspend fun scheduleCall(
        userId: String,
        userName: String,
        userEmail: String,
        preferredDate: String,
        preferredTime: String,
        note: String
    ) {
        val requestId = "req_${System.currentTimeMillis()}"
        val entity = CallRequestEntity(
            id = requestId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            preferredDate = preferredDate,
            preferredTime = preferredTime,
            reasonNote = note,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )

        // 1. Offline-First: Write to Room DB immediately
        callRequestDao.insertCallRequest(entity)

        // 2. Background cloud sync to Firestore
        syncCallRequestToFirestoreInBackground(entity.toDomainModel())
    }

    override suspend fun updateRequestStatus(requestId: String, status: String) {
        callRequestDao.updateRequestStatus(requestId, status)
        if (firestore != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firestore.collection("call_requests").document(requestId).update("status", status)
                } catch (e: Exception) { }
            }
        }
    }

    private fun syncCallRequestToFirestoreInBackground(request: CallRequest) {
        if (firestore == null) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reqMap = mapOf(
                    "id" to request.id,
                    "userId" to request.userId,
                    "userName" to request.userName,
                    "userEmail" to request.userEmail,
                    "preferredDate" to request.preferredDate,
                    "preferredTime" to request.preferredTime,
                    "reasonNote" to request.reasonNote,
                    "status" to request.status,
                    "timestamp" to request.timestamp
                )
                firestore.collection("call_requests").document(request.id).set(reqMap)
            } catch (e: Exception) { }
        }
    }
}
