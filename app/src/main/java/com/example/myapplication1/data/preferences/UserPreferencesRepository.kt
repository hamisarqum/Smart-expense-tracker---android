package com.example.myapplication1.data.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val CURRENCY_CODE = stringPreferencesKey("currency_code")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        
        // Notifications
        val BUDGET_NOTIFICATIONS_ENABLED = booleanPreferencesKey("budget_notifications_enabled")
        val WARNING_80_ENABLED = booleanPreferencesKey("warning_80_enabled")
        val EXCEEDED_ALERT_ENABLED = booleanPreferencesKey("exceeded_alert_enabled")
        val NO_BUDGET_REMINDER_ENABLED = booleanPreferencesKey("no_budget_reminder_enabled")
        
        // Notification tracking
        val LAST_MONTH_WARNED_80 = stringPreferencesKey("last_month_warned_80")
        val LAST_MONTH_WARNED_100 = stringPreferencesKey("last_month_warned_100")
        val LAST_MONTH_NO_BUDGET_REMINDED = stringPreferencesKey("last_month_no_budget_reminded")
    }

    private val TAG = "UserPreferencesRepo"

    val userPreferencesStream: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            UserPreferences(
                userName = preferences[PreferencesKeys.USER_NAME] ?: "User",
                currencyCode = preferences[PreferencesKeys.CURRENCY_CODE] ?: "GBP",
                themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM",
                budgetNotificationsEnabled = preferences[PreferencesKeys.BUDGET_NOTIFICATIONS_ENABLED] ?: true,
                warning80Enabled = preferences[PreferencesKeys.WARNING_80_ENABLED] ?: true,
                exceededAlertEnabled = preferences[PreferencesKeys.EXCEEDED_ALERT_ENABLED] ?: true,
                noBudgetReminderEnabled = preferences[PreferencesKeys.NO_BUDGET_REMINDER_ENABLED] ?: false,
                lastMonthWarned80 = preferences[PreferencesKeys.LAST_MONTH_WARNED_80] ?: "",
                lastMonthWarned100 = preferences[PreferencesKeys.LAST_MONTH_WARNED_100] ?: "",
                lastMonthNoBudgetReminded = preferences[PreferencesKeys.LAST_MONTH_NO_BUDGET_REMINDED] ?: ""
            )
        }

    suspend fun updateUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = userName
        }
    }

    suspend fun updateCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY_CODE] = currencyCode
        }
    }

    suspend fun updateThemeMode(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode
        }
    }

    suspend fun updateBudgetNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.BUDGET_NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun updateWarning80Enabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.WARNING_80_ENABLED] = enabled }
    }

    suspend fun updateExceededAlertEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.EXCEEDED_ALERT_ENABLED] = enabled }
    }

    suspend fun updateNoBudgetReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.NO_BUDGET_REMINDER_ENABLED] = enabled }
    }

    suspend fun updateLastMonthWarned80(monthKey: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.LAST_MONTH_WARNED_80] = monthKey }
    }

    suspend fun updateLastMonthWarned100(monthKey: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.LAST_MONTH_WARNED_100] = monthKey }
    }

    suspend fun updateLastMonthNoBudgetReminded(monthKey: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.LAST_MONTH_NO_BUDGET_REMINDED] = monthKey }
    }

    suspend fun resetPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class UserPreferences(
    val userName: String,
    val currencyCode: String,
    val themeMode: String,
    val budgetNotificationsEnabled: Boolean,
    val warning80Enabled: Boolean,
    val exceededAlertEnabled: Boolean,
    val noBudgetReminderEnabled: Boolean,
    val lastMonthWarned80: String,
    val lastMonthWarned100: String,
    val lastMonthNoBudgetReminded: String
)
