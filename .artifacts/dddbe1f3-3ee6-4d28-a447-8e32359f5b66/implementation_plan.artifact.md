# Implementation Plan - Phase 5: Final Polish & Release Prep

This plan covers polishing the Smart Expense Tracker app for stability, accessibility, and professional appearance, preparing it for a release build.

## Proposed Changes

### 1. Build & Dependencies
- [MODIFY] `libs.versions.toml`: Add `androidx.core:core-splashscreen`.
- [MODIFY] `app/build.gradle.kts`:
    - Add Splashscreen dependency.
    - Set `versionCode = 1` and `versionName = "1.0.0"`.
    - Configure Release build type with basic minification (tested).

### 2. Branding & Resources
- [MODIFY] `strings.xml`:
    - Update `app_name` to "Smart Expense Tracker".
    - Remove old Baking/Gemini strings.
    - Centralize all UI strings (Screen titles, labels, buttons).
- [MODIFY] `res/drawable`: Replace default adaptive icon layers with financial-themed designs (Wallet/Chart concept).
- [NEW] `res/values/styles.xml`: Define `Theme.App.Starting` for the Splash Screen API.

### 3. UI/UX Polishing
- [NEW] `ui/theme/Dimensions.kt`: Define shared padding, corner radius, and spacing values.
- [MODIFY] `MainActivity.kt`: Integrate `installSplashScreen()`.
- [MODIFY] All Screens:
    - Apply consistent dimensions.
    - Add accessibility content descriptions.
    - Ensure proper empty/loading states.
    - Polish typography and color usage (Material 3).

### 4. Stability & Code Quality
- [DELETE] `BakingScreen.kt`, `BakingViewModel.kt`, `UiState.kt` (Phase 1 placeholders).
- [MODIFY] `TransactionViewModel.kt` & `BudgetViewModel.kt`: Refine error handling and loading states.
- [MODIFY] `CsvExporter.kt` & `PdfReportGenerator.kt`: Ensure background thread execution and robust error reporting.

### 5. Verification & Testing
- [NEW] `test/`: Add unit tests for budget logic, currency formatting, and date range calculations.
- [NEW] `androidTest/`: Add basic UI tests for Home and navigation.
- [VERIFY] Room Migration 1 -> 2.
- [VERIFY] Lint report for accessibility and performance issues.

## User Review Required

> [!IMPORTANT]
> I will be removing the initial "Baking" sample files to clean up the project.
>
> **Icons**: I will generate simplified vector-based launcher icons.
>
> **Release Build**: I will enable `minifyEnabled` in the release build to test shrinking, but I will provide instructions for you to generate the final signed bundle.

## Verification Plan

### Automated Tests
- Run `gradlew.bat clean kspDebugKotlin testDebugUnitTest lintDebug assembleDebug assembleRelease`.

### Manual Verification
- **Splash Screen**: Confirm the app launches with the new icon and background.
- **Accessibility**: Test with TalkBack on key screens (Home, Add Transaction).
- **Responsiveness**: Verify layout on a small screen (e.g., Pixel 4) and large screen (e.g., Tablet).
- **Migration**: Re-verify version 1 to 2 upgrade path.
