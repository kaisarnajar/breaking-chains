/**
 * Standalone Node.js Seeder Script for Breaking Chains Firestore Database
 * 
 * Usage:
 * 1. Ensure node.js is installed.
 * 2. Run: npm install firebase-admin
 * 3. Export GOOGLE_APPLICATION_CREDENTIALS="path/to/serviceAccountKey.json"
 * 4. Run: node scripts/seed-firestore.js
 */

const admin = require('firebase-admin');

// Initialize Firebase Admin (uses default credentials or project config)
if (!admin.apps.length) {
  admin.initializeApp({
    projectId: 'breaking-chains'
  });
}

const db = admin.firestore();

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

  console.log("🌱 Seeding mock users...");
  await db.collection('users').doc('u_elena_101').set({
    id: "u_elena_101",
    name: "Elena Richardson",
    email: "elena.r@example.com",
    role: "USER",
    activeStreakDays: 124,
    joinedDate: "October 12, 2023",
    totalCheckIns: 342,
    successRate: 98
  });

  await db.collection('users').doc('u_admin_999').set({
    id: "u_admin_999",
    name: "Kaisar Admin",
    email: "kaisarnajar11114@gmail.com",
    role: "ADMIN",
    activeStreakDays: 365,
    joinedDate: "January 1, 2023",
    totalCheckIns: 500,
    successRate: 100
  });

  console.log("🌱 Seeding mock relapse logs...");
  const logs = [
    {
      id: "log_101",
      userId: "u_elena_101",
      timestamp: 1696944000000,
      trigger: "Stress",
      note: "Overwhelmed after work deadline.",
      moodBefore: "Stressed",
      severity: 2
    },
    {
      id: "log_102",
      userId: "u_elena_101",
      timestamp: 1695552000000,
      trigger: "Social Urge",
      note: "Weekend gathering temptation.",
      moodBefore: "Anxious",
      severity: 1
    }
  ];

  for (const log of logs) {
    await db.collection('relapse_logs').doc(log.id).set(log);
  }

  console.log("🌱 Seeding mock call requests...");
  await db.collection('call_requests').doc('req_201').set({
    id: "req_201",
    userId: "u_elena_101",
    userName: "Elena Richardson",
    userEmail: "elena.r@example.com",
    preferredDate: "2026-07-25",
    preferredTime: "14:00",
    reasonNote: "Requesting guidance on managing work-related stress triggers.",
    status: "PENDING",
    timestamp: Date.now()
  });

  console.log("🎉 Firestore successfully wiped and seeded with mock test data!");
  process.exit(0);
}

seedFirestore().catch(err => {
  console.error("❌ Error seeding Firestore:", err);
  process.exit(1);
});
