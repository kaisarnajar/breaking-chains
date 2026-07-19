package com.breakingchains.app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreSeeder {

    suspend fun resetAndSeedDatabase(firestore: FirebaseFirestore): Result<String> {
        return try {
            // Step 1: Wipe existing collections
            wipeCollection(firestore, "users")
            wipeCollection(firestore, "relapse_logs")
            wipeCollection(firestore, "call_requests")

            // Step 2: Seed Massive List of Users (10 Users + Admin)
            val users = listOf(
                mapOf(
                    "id" to "u_elena_101",
                    "name" to "Elena Richardson",
                    "email" to "elena.r@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 124,
                    "joinedDate" to "October 12, 2023",
                    "totalCheckIns" to 342,
                    "successRate" to 98
                ),
                mapOf(
                    "id" to "u_marcus_102",
                    "name" to "Marcus Vance",
                    "email" to "marcus.vance@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 45,
                    "joinedDate" to "November 5, 2023",
                    "totalCheckIns" to 120,
                    "successRate" to 92
                ),
                mapOf(
                    "id" to "u_sophia_103",
                    "name" to "Sophia Chen",
                    "email" to "sophia.chen@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 7,
                    "joinedDate" to "January 15, 2024",
                    "totalCheckIns" to 28,
                    "successRate" to 85
                ),
                mapOf(
                    "id" to "u_david_104",
                    "name" to "David Miller",
                    "email" to "david.m@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 3,
                    "joinedDate" to "February 1, 2024",
                    "totalCheckIns" to 14,
                    "successRate" to 78
                ),
                mapOf(
                    "id" to "u_hannah_105",
                    "name" to "Hannah Abbott",
                    "email" to "hannah.a@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 90,
                    "joinedDate" to "December 1, 2023",
                    "totalCheckIns" to 210,
                    "successRate" to 95
                ),
                mapOf(
                    "id" to "u_james_106",
                    "name" to "James Wilson",
                    "email" to "james.w@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 180,
                    "joinedDate" to "August 10, 2023",
                    "totalCheckIns" to 400,
                    "successRate" to 99
                ),
                mapOf(
                    "id" to "u_olivia_107",
                    "name" to "Olivia Taylor",
                    "email" to "olivia.t@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 12,
                    "joinedDate" to "March 20, 2024",
                    "totalCheckIns" to 50,
                    "successRate" to 88
                ),
                mapOf(
                    "id" to "u_liam_108",
                    "name" to "Liam Johnson",
                    "email" to "liam.j@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 1,
                    "joinedDate" to "April 2, 2024",
                    "totalCheckIns" to 5,
                    "successRate" to 60
                ),
                mapOf(
                    "id" to "u_emma_109",
                    "name" to "Emma Watson",
                    "email" to "emma.w@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 210,
                    "joinedDate" to "July 1, 2023",
                    "totalCheckIns" to 480,
                    "successRate" to 100
                ),
                mapOf(
                    "id" to "u_noah_110",
                    "name" to "Noah Smith",
                    "email" to "noah.s@example.com",
                    "role" to "USER",
                    "activeStreakDays" to 60,
                    "joinedDate" to "January 2, 2024",
                    "totalCheckIns" to 150,
                    "successRate" to 91
                ),
                mapOf(
                    "id" to "u_admin_999",
                    "name" to "Kaisar Admin",
                    "email" to "kaisarnajar11114@gmail.com",
                    "role" to "ADMIN",
                    "activeStreakDays" to 365,
                    "joinedDate" to "January 1, 2023",
                    "totalCheckIns" to 500,
                    "successRate" to 100
                )
            )

            for (user in users) {
                val id = user["id"] as String
                firestore.collection("users").document(id).set(user).await()
            }

            // Step 3: Seed Massive List of Relapse Logs (25+ Entries)
            val triggers = listOf("Stress", "Social Urge", "Boredom", "Fatigue", "Emotional Low", "Environmental", "Loneliness")
            val moods = listOf("Stressed", "Anxious", "Tired", "Angry", "Sad", "Neutral")
            val sampleNotes = listOf(
                "Overwhelmed after work deadline. Needed better coping routine.",
                "Weekend party temptation. Was feeling peer pressure.",
                "Late night boredom while scrolling social media.",
                "Felt lonely during long weekend.",
                "Stressful argument with family member.",
                "Exhausted after travel, lowered resistance.",
                "Felt anxious about upcoming presentation.",
                "Idle afternoon with no planned activities."
            )

            val baseTime = System.currentTimeMillis()
            val dayMillis = 86400000L

            val logs = mutableListOf<Map<String, Any>>()
            var logCounter = 1

            for (user in users.filter { it["role"] == "USER" }) {
                val userId = user["id"] as String
                // Create 3 to 5 historical logs for each user
                for (i in 1..4) {
                    val timestamp = baseTime - (i * 7 * dayMillis) - (i * 3600000L)
                    logs.add(
                        mapOf(
                            "id" to "log_${logCounter++}",
                            "userId" to userId,
                            "timestamp" to timestamp,
                            "trigger" to triggers[(logCounter + i) % triggers.size],
                            "note" to sampleNotes[(logCounter + i) % sampleNotes.size],
                            "moodBefore" to moods[(logCounter + i) % moods.size],
                            "severity" to ((i % 3) + 1)
                        )
                    )
                }
            }

            for (log in logs) {
                val id = log["id"] as String
                firestore.collection("relapse_logs").document(id).set(log).await()
            }

            // Step 4: Seed Massive List of Call Requests (8 Requests)
            val callRequests = listOf(
                mapOf(
                    "id" to "req_201",
                    "userId" to "u_elena_101",
                    "userName" to "Elena Richardson",
                    "userEmail" to "elena.r@example.com",
                    "preferredDate" to "2026-07-25",
                    "preferredTime" to "14:00",
                    "reasonNote" to "Requesting guidance on managing work-related stress triggers.",
                    "status" to "PENDING",
                    "timestamp" to baseTime - dayMillis
                ),
                mapOf(
                    "id" to "req_202",
                    "userId" to "u_marcus_102",
                    "userName" to "Marcus Vance",
                    "userEmail" to "marcus.vance@example.com",
                    "preferredDate" to "2026-07-26",
                    "preferredTime" to "10:30",
                    "reasonNote" to "Discussing 45-day milestone strategy and social urges.",
                    "status" to "CONFIRMED",
                    "timestamp" to baseTime - (2 * dayMillis)
                ),
                mapOf(
                    "id" to "req_203",
                    "userId" to "u_sophia_103",
                    "userName" to "Sophia Chen",
                    "userEmail" to "sophia.chen@example.com",
                    "preferredDate" to "2026-07-27",
                    "preferredTime" to "16:00",
                    "reasonNote" to "Need emergency advice following recent relapse.",
                    "status" to "PENDING",
                    "timestamp" to baseTime - (3 * dayMillis)
                ),
                mapOf(
                    "id" to "req_204",
                    "userId" to "u_hannah_105",
                    "userName" to "Hannah Abbott",
                    "userEmail" to "hannah.a@example.com",
                    "preferredDate" to "2026-07-20",
                    "preferredTime" to "11:00",
                    "reasonNote" to "Quarterly recovery review session.",
                    "status" to "COMPLETED",
                    "timestamp" to baseTime - (5 * dayMillis)
                ),
                mapOf(
                    "id" to "req_205",
                    "userId" to "u_james_106",
                    "userName" to "James Wilson",
                    "userEmail" to "james.w@example.com",
                    "preferredDate" to "2026-07-28",
                    "preferredTime" to "15:30",
                    "reasonNote" to "Mentorship advice for helping newcomers.",
                    "status" to "PENDING",
                    "timestamp" to baseTime - (6 * dayMillis)
                )
            )

            for (req in callRequests) {
                val id = req["id"] as String
                firestore.collection("call_requests").document(id).set(req).await()
            }

            Result.success("Firestore successfully wiped and populated with massive dataset (11 Users, 40 Logs, 5 Call Requests)!")
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
