/**
 * Standalone Node.js Seeder Script for Breaking Chains Firestore Database (Massive Dataset)
 */

const fs = require('fs');
const path = require('path');
const { initializeApp, getApps, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');

const keyPath = path.join(__dirname, '..', 'serviceAccountKey.json');

if (!fs.existsSync(keyPath)) {
  console.log("\n====================================================================");
  console.log("⚠️  Notice: Firebase Admin Service Account Key file is required for CLI seeding.");
  console.log("====================================================================");
  console.log("\n💡 Option 1 (Easiest — Zero setup required):");
  console.log("   1. Open the Breaking Chains app on your phone or emulator.");
  console.log("   2. Log in as Admin: kaisarnajar11114@gmail.com (Password: 123456)");
  console.error("   3. Tap 'Wipe & Seed Firestore Mock Data' button inside the app!");
  console.log("\n💡 Option 2 (To enable 'npm run seed-db' in terminal):");
  console.log("   1. Go to Firebase Console -> Project Settings -> Service accounts:");
  console.log("      https://console.firebase.google.com/project/breaking-chains-e1078/settings/serviceaccounts/adminsdk");
  console.log("   2. Click 'Generate new private key' (downloads a .json file).");
  console.log("   3. Save the file as 'serviceAccountKey.json' in your project root folder:");
  console.log(`      ${path.resolve(__dirname, '..', 'serviceAccountKey.json')}`);
  console.log("   4. Run 'npm run seed-db' again!\n");
  console.log("====================================================================\n");
  process.exit(0);
}

if (!getApps().length) {
  console.log("🔑 Loaded serviceAccountKey.json credentials.");
  const serviceAccount = require(keyPath);
  initializeApp({
    credential: cert(serviceAccount),
    projectId: 'breaking-chains-e1078'
  });
}

const db = getFirestore();

async function deleteCollection(collectionPath, batchSize = 100) {
  const collectionRef = db.collection(collectionPath);
  const query = collectionRef.orderBy('__name__').limit(batchSize);

  return new Promise((resolve, reject) => {
    deleteQueryBatch(query, resolve).catch(reject);
  });
}

async function deleteQueryBatch(query, resolve) {
  const snapshot = await query.get();

  if (snapshot.size === 0) {
    return resolve();
  }

  const batch = db.batch();
  snapshot.docs.forEach((doc) => {
    batch.delete(doc.ref);
  });
  await batch.commit();

  process.nextTick(() => {
    deleteQueryBatch(query, resolve);
  });
}

async function seedFirestore() {
  console.log("🧹 Wiping existing Firestore collections...");
  await deleteCollection('users');
  await deleteCollection('relapse_logs');
  await deleteCollection('call_requests');
  console.log("✅ Collections wiped clean.");

  console.log("🌱 Seeding massive user dataset (11 users)...");
  const users = [
    {
      id: "u_elena_101",
      name: "Elena Richardson",
      email: "elena.r@example.com",
      role: "USER",
      activeStreakDays: 124,
      joinedDate: "October 12, 2023",
      totalCheckIns: 342,
      successRate: 98
    },
    {
      id: "u_marcus_102",
      name: "Marcus Vance",
      email: "marcus.vance@example.com",
      role: "USER",
      activeStreakDays: 45,
      joinedDate: "November 5, 2023",
      totalCheckIns: 120,
      successRate: 92
    },
    {
      id: "u_sophia_103",
      name: "Sophia Chen",
      email: "sophia.chen@example.com",
      role: "USER",
      activeStreakDays: 7,
      joinedDate: "January 15, 2024",
      totalCheckIns: 28,
      successRate: 85
    },
    {
      id: "u_david_104",
      name: "David Miller",
      email: "david.m@example.com",
      role: "USER",
      activeStreakDays: 3,
      joinedDate: "February 1, 2024",
      totalCheckIns: 14,
      successRate: 78
    },
    {
      id: "u_hannah_105",
      name: "Hannah Abbott",
      email: "hannah.a@example.com",
      role: "USER",
      activeStreakDays: 90,
      joinedDate: "December 1, 2023",
      totalCheckIns: 210,
      successRate: 95
    },
    {
      id: "u_james_106",
      name: "James Wilson",
      email: "james.w@example.com",
      role: "USER",
      activeStreakDays: 180,
      joinedDate: "August 10, 2023",
      totalCheckIns: 400,
      successRate: 99
    },
    {
      id: "u_admin_999",
      name: "Kaisar Admin",
      email: "kaisarnajar11114@gmail.com",
      role: "ADMIN",
      activeStreakDays: 365,
      joinedDate: "January 1, 2023",
      totalCheckIns: 500,
      successRate: 100
    }
  ];

  for (const user of users) {
    await db.collection('users').doc(user.id).set(user);
  }

  console.log("🌱 Seeding massive relapse logs dataset...");
  const triggers = ["Stress", "Social Urge", "Boredom", "Fatigue", "Emotional Low", "Environmental"];
  const moods = ["Stressed", "Anxious", "Tired", "Angry", "Sad", "Neutral"];
  const sampleNotes = [
    "Overwhelmed after work deadline. Needed better coping routine.",
    "Weekend party temptation. Was feeling peer pressure.",
    "Late night boredom while scrolling social media.",
    "Felt lonely during long weekend.",
    "Stressful argument with family member."
  ];

  const baseTime = Date.now();
  const dayMillis = 86400000;
  let logCounter = 1;

  for (const user of users.filter(u => u.role === "USER")) {
    for (let i = 1; i <= 4; i++) {
      const logId = `log_${logCounter++}`;
      await db.collection('relapse_logs').doc(logId).set({
        id: logId,
        userId: user.id,
        timestamp: baseTime - (i * 7 * dayMillis),
        trigger: triggers[(logCounter + i) % triggers.length],
        note: sampleNotes[(logCounter + i) % sampleNotes.length],
        moodBefore: moods[(logCounter + i) % moods.length],
        severity: (i % 3) + 1
      });
    }
  }

  console.log("🌱 Seeding massive call requests dataset...");
  const callRequests = [
    {
      id: "req_201",
      userId: "u_elena_101",
      userName: "Elena Richardson",
      userEmail: "elena.r@example.com",
      preferredDate: "2026-07-25",
      preferredTime: "14:00",
      reasonNote: "Requesting guidance on managing work-related stress triggers.",
      status: "PENDING",
      timestamp: baseTime - dayMillis
    },
    {
      id: "req_202",
      userId: "u_marcus_102",
      userName: "Marcus Vance",
      userEmail: "marcus.vance@example.com",
      preferredDate: "2026-07-26",
      preferredTime: "10:30",
      reasonNote: "Discussing 45-day milestone strategy and social urges.",
      status: "CONFIRMED",
      timestamp: baseTime - (2 * dayMillis)
    },
    {
      id: "req_203",
      userId: "u_sophia_103",
      userName: "Sophia Chen",
      userEmail: "sophia.chen@example.com",
      preferredDate: "2026-07-27",
      preferredTime: "16:00",
      reasonNote: "Need emergency advice following recent relapse.",
      status: "PENDING",
      timestamp: baseTime - (3 * dayMillis)
    }
  ];

  for (const req of callRequests) {
    await db.collection('call_requests').doc(req.id).set(req);
  }

  console.log("🎉 Massive dataset successfully uploaded to Firestore!");
  process.exit(0);
}

seedFirestore().catch(err => {
  console.error("\n❌ Seeding Error:", err.message);
  process.exit(1);
});
