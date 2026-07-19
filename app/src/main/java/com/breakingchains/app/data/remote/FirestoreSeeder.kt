package com.breakingchains.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreSeeder {

    suspend fun resetAndSeedDatabase(firestore: FirebaseFirestore): Result<String> {
        return try {
            // Step 1: Wipe existing collections (users, relapse_logs, call_requests)
            wipeCollection(firestore, "users")
            wipeCollection(firestore, "relapse_logs")
            wipeCollection(firestore, "call_requests")

            // Step 2: Seed Mock Users
            val userElena = mapOf(
                "id" to "u_elena_101",
                "name" to "Elena Richardson",
                "email" to "elena.r@example.com",
                "role" to "USER",
                "activeStreakDays" to 124,
                "joinedDate" to "October 12, 2023",
                "totalCheckIns" to 342,
                "successRate" to 98
            )

            val userAdmin = mapOf(
                "id" to "u_admin_999",
                "name" to "Kaisar Admin",
                "email" to "kaisarnajar11114@gmail.com",
                "role" to "ADMIN",
                "activeStreakDays" to 365,
                "joinedDate" to "January 1, 2023",
                "totalCheckIns" to 500,
                "successRate" to 100
            )

            firestore.collection("users").document("u_elena_101").set(userElena).await()
            firestore.collection("users").document("u_admin_999").set(userAdmin).await()

            // Step 3: Seed Relapse Logs for Elena (Matching Reference Images)
            val logs = listOf(
                mapOf(
                    "id" to "log_101",
                    "userId" to "u_elena_101",
                    "timestamp" to 1696944000000L, // Oct 10, 2023
                    "trigger" to "Stress",
                    "note" to "Overwhelmed after work deadline. Need better coping routine.",
                    "moodBefore" to "Stressed",
                    "severity" to 2
                ),
                mapOf(
                    "id" to "log_102",
                    "userId" to "u_elena_101",
                    "timestamp" to 1695552000000L, // Sep 24, 2023
                    "trigger" to "Social Urge",
                    "note" to "Weekend gathering temptation.",
                    "moodBefore" to "Anxious",
                    "severity" to 1
                ),
                mapOf(
                    "id" to "log_103",
                    "userId" to "u_elena_101",
                    "timestamp" to 1692096000000L, // Aug 15, 2023
                    "trigger" to "Boredom",
                    "note" to "Late night screen time.",
                    "moodBefore" to "Tired",
                    "severity" to 1
                )
            )

            for (log in logs) {
                val id = log["id"] as String
                firestore.collection("relapse_logs").document(id).set(log).await()
            }

            // Step 4: Seed Call Requests
            val callRequest = mapOf(
                "id" to "req_201",
                "userId" to "u_elena_101",
                "userName" to "Elena Richardson",
                "userEmail" to "elena.r@example.com",
                "preferredDate" to "2026-07-25",
                "preferredTime" to "14:00",
                "reasonNote" to "Requesting guidance on managing work-related stress triggers.",
                "status" to "PENDING",
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("call_requests").document("req_201").set(callRequest).await()

            Result.success("Firestore successfully wiped and populated with mock test data!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun wipeCollection(firestore: FirebaseFirestore, collectionName: String) {
        val snapshot = firestore.collection(collectionName).get().await()
        for (document in snapshot.documents) {
            firestore.collection(collectionName).document(document.id).delete().await()
        }
    }
}
