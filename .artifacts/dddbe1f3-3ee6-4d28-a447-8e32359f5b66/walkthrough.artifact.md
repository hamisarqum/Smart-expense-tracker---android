# Walkthrough - Phase 4: Advanced Reports, Charts & Exports

Phase 4 of the **Smart Expense Tracker** is now complete. This phase enhances the app with advanced financial visualization, automated alerts, and professional reporting capabilities.

## Changes Made

### 1. Advanced Financial Visualization
- **Canvas Charts**: Implemented 100% native Donut (Categories), Bar (Income vs Expense), and Line (Spending Trend) charts using Jetpack Compose Canvas.
- **Period Filtering**: Users can now filter reports by:
    - Current Month / Previous Month
    - Last 3 Months / Last 6 Months
    - Current Year
    - **Custom Date Range** using a Material 3 DateRangePicker.
- **Period Comparison**: Automatically compares current spending and income against the equivalent previous period with percentage changes.
- **Rule-based Insights**: Generates observations like "Spending increased by X%" or "Food is your top category."

### 2. Automated Budget Alerts
- **WorkManager Integration**: A daily background task (`BudgetNotificationWorker`) checks spending progress.
- **Threshold Alerts**: Sends system notifications when:
    - Spending reaches **80%** of the budget.
    - Spending **exceeds 100%** of the budget.
- **Preferences**: Users can toggle notifications and specific alerts in the Settings screen.
- **Android 13+ Support**: Implemented runtime `POST_NOTIFICATIONS` permission handling.

### 3. Professional Export & Sharing
- **CSV Export**: Generates a detailed transaction log for the selected period.
- **PDF Report**: Creates a multi-page, formatted PDF summary including user name, period totals, and a transaction table.
- **File Sharing**: Uses `FileProvider` to securely share exported reports through the standard Android Sharesheet.

### 4. Technical Enhancements
- **Room Aggregation**: Added optimized DAO queries to calculate totals, averages, and groupings directly in SQLite.
- **Reports Repository**: Centralized logic for multi-data-source report generation.
- **Export ViewModels**: Reactive handling of export progress and file state.

## Verification Results
- **Build**: `./gradlew assembleDebug` passed successfully.
- **Migration**: Database schema remains stable at version 2 (no changes needed for new DAO queries).
- **Persistence**: Verified that notification settings and custom date ranges are handled correctly.

## How to Test
1. **Try Charts**: Go to the **Reports** screen and switch between different periods (e.g., "Last 6 Months"). Watch the donut and trend charts update.
2. **Custom Range**: Scroll to the top of Reports, select "Custom Range", and pick dates.
3. **Export**: Tap **CSV** or **PDF** in the Export section. Once finished, tap **Share** to send the file.
4. **Notifications**: In Settings, ensure "Budget Notifications" is ON. The system will check your budget daily. (For manual testing, you can set a very low budget to trigger the 80% warning immediately in the background).
5. **Permissions**: When enabling notifications on Android 13+, verify the system permission dialog appears correctly.
