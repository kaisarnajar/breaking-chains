# Breaking Chains ⛓️

**Breaking Chains** is an Android application designed to help individuals track their addiction recovery journey, monitor sobriety milestones, log relapses with triggers and notes, and connect directly with mentors or administrators through scheduled call sessions.

---

## 🌟 Features

- ⏱️ **Sobriety & Relapse Tracker**: Keep track of clean time, log relapse occurrences along with triggers, feelings, and recovery notes.
- 👥 **Admin Supervision & Overview**: Administrators can review user recovery progress, view relapse logs, and monitor overall user well-being.
- 📞 **Scheduled Admin Calls**: Users can request and schedule direct guidance calls with their designated admin/mentor.
- 🎨 **Modern Material 3 UI**: Built using Jetpack Compose with a calming, accessible design system tailored for healing and growth.

---

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3
- **Asynchronous & Concurrency**: Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & `StateFlow`
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/guide/navigation)
- **Architecture**: MVVM / Clean Architecture

---

## 📁 Package Structure

```
com.breakingchains.app
 ├── data/                # Data sources, Repositories, and Data Models
 ├── domain/              # Business logic & Use Cases
 └── ui/
      ├── components/     # Reusable Compose UI components
      ├── navigation/     # NavHost & Route definitions
      ├── screens/        # Feature screens (Tracker, Relapse Log, Schedule Call, Admin)
      └── theme/          # Color, Type, and Material3 Theme definitions
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17 / JDK 11
- Android SDK 24+ (Android 7.0 or higher)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/kaisarnajar/breaking-chains.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies and run on an emulator or physical device.

---

## 📄 License
This project is open-source under the MIT License.
