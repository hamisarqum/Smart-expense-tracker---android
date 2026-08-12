# Smart Expense Tracker

## Project Overview

Smart Expense Tracker is a native Android application for recording income and expenses, managing monthly budgets, and reviewing spending patterns. The app follows a local-first approach, so core financial records are stored on the device without requiring a bank connection or user account.

## Key Features

- Add, edit, delete, search, and filter transactions
- Track income, expenses, and current balance
- Create and manage monthly budgets
- Display budget progress and warning states
- Generate reports across different date ranges
- View category, trend, and comparison charts
- Export report data as CSV
- Generate PDF reports
- Share exported files through the Android Sharesheet
- Receive budget warning and exceeded notifications
- Select currency and user name preferences
- Choose light, dark, or system theme
- Store transactions, budgets, and preferences locally

## Main Screens

- Home dashboard
- Transactions
- Add and edit transaction
- Monthly budget
- Reports and charts
- Settings

## Technology Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose and Material 3
- **Architecture:** MVVM-style separation with repositories and ViewModels
- **Local Database:** Room
- **Persistence:** Preferences DataStore
- **Navigation:** Navigation Compose
- **Background Work:** WorkManager
- **Code Generation:** KSP
- **Export:** Android PdfDocument and CSV generation
- **File Sharing:** FileProvider and Android Sharesheet
- **Build System:** Gradle Kotlin DSL

The application schedules periodic budget checks with WorkManager and uses local repositories for transaction, budget, report, and preference data.

## Project Structure

```text
app/src/main/java/com/example/myapplication1/
|-- data/            Room database, DataStore, repositories, and app container
|-- export/          CSV and PDF export logic
|-- model/           Application data models
|-- navigation/      Routes and navigation setup
|-- notifications/   Budget notification worker and helpers
|-- ui/              Screens, reusable components, and theme
|-- utils/           Date, currency, and calculation utilities
|-- viewmodel/       UI state and business logic
|-- MainActivity.kt
`-- ExpenseTrackerApplication.kt
```

## Data and Privacy

- Financial records are stored locally on the device.
- The app does not connect directly to bank accounts.
- Transaction records are not uploaded to a remote application server as part of the core expense-tracking workflow.
- Exported files are created only when requested by the user.
- Shared files use Android content URIs through FileProvider.

## Getting Started

### Requirements

- Android Studio
- Android SDK
- JDK supported by the configured Android Gradle Plugin
- Android emulator or physical Android device

### Run the App

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronize the project.
4. Start an emulator or connect an Android device with USB debugging enabled.
5. Select the device and run the `app` configuration.

### Build from the Command Line on Windows

```bash
gradlew.bat clean
gradlew.bat kspDebugKotlin
gradlew.bat assembleDebug
```

The debug APK is generated under:

```text
app/build/outputs/apk/debug/
```

## Testing and Verification

The app was developed through iterative implementation, debugging, Gradle build verification, emulator testing, and physical-device testing. Functional checks covered transaction management, budgets, preferences, reports, notifications, CSV and PDF export, and file sharing.

Automated feature-test coverage is not currently included in the repository and can be added in a future iteration.

## Development Assistance

Cursor AI was used during planning, coding, debugging, and refinement. Suggested changes were reviewed and tested before being incorporated into the application.

## Current Build Configuration

- Minimum Android SDK: 24
- Target Android SDK: 36
- Application version: 1.0.0
- Release builds use code shrinking and resource shrinking

## License

This project was created for educational and portfolio purposes.

## Contact

**Muhammad Hamis Arqum**

[LinkedIn Profile](https://www.linkedin.com/in/hamis-arqum/)
