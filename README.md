Smart Expense Tracker

Smart Expense Tracker is a native Android application for recording income and expenses, managing monthly budgets, and understanding spending patterns. It is designed as a practical, local-first solution for users who want simple financial tracking without connecting a bank account or creating an online account.

Key Features

Add, edit, delete, search, and filter transactions

Track income, expenses, and current balance

Set and manage monthly budgets

View budget progress and warning states

Generate reports for different date ranges

Display category, trend, and comparison charts

Export selected reports as CSV and PDF

Share exported files through the Android Sharesheet

Receive budget warning and exceeded notifications

Select currency, user name, and light, dark, or system theme

Store transactions, budgets, and preferences locally

Screens

Home dashboard

Transactions

Add and edit transaction

Monthly budget

Reports and charts

Settings

Technology Stack

Kotlin

Jetpack Compose

Material 3

MVVM architecture

Room Database

KSP

Preferences DataStore

Navigation Compose

WorkManager

Android PdfDocument

FileProvider

Cursor AI with Gemini API support during development

Project Structure

data/           Room database, DataStore, and repositories
model/          Application data models
navigation/     Routes and navigation setup
notifications/  Budget notification worker and helpers
export/         CSV, PDF, and file-sharing services
ui/             Screens, components, and theme
viewmodel/      UI state and business logic
utils/          Date, currency, and calculation utilities

Getting Started

Requirements

Android Studio

Android SDK

Git

Android emulator or physical Android device

Run the App

Clone or download this repository.

Open the project in Android Studio.

Allow Gradle to synchronize the project.

Connect an Android phone with USB debugging enabled, or start an emulator.

Select the device and click Run.

Build on Windows

gradlew.bat clean
gradlew.bat kspDebugKotlin
gradlew.bat assembleDebug

The debug APK is generated under:

app/build/outputs/apk/debug/

Data and Privacy

Financial data is stored locally on the device.

The app does not connect to bank accounts.

Transactions are not uploaded to a server.

Exported files are created only when requested by the user.

Shared files use secure Android content URIs.

Testing

The application was developed through iterative planning, implementation, debugging, and build verification. Testing covers transaction management, budget calculations, preference persistence, reports, notifications, CSV and PDF exports, file sharing, and real-device execution through USB.

Development Assistance

Cursor AI was used during planning, coding, debugging, and refinement. Gemini API support was also used to help analyze issues and improve the implementation. All generated suggestions were reviewed and tested before inclusion.

License

This project was created for educational purposes.
