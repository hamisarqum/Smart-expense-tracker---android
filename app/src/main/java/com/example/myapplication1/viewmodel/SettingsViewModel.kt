package com.example.myapplication1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.data.preferences.UserPreferences
import com.example.myapplication1.data.preferences.UserPreferencesRepository
import com.example.myapplication1.data.repository.BudgetRepository
import com.example.myapplication1.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(
                "User", "GBP", "SYSTEM",
                true, true, true, false,
                "", "", ""
            )
        )

    fun updateUserName(name: String) {
        viewModelScope.launch { userPreferencesRepository.updateUserName(name) }
    }

    fun updateCurrency(code: String) {
        viewModelScope.launch { userPreferencesRepository.updateCurrency(code) }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch { userPreferencesRepository.updateThemeMode(mode) }
    }

    fun updateBudgetNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateBudgetNotificationsEnabled(enabled) }
    }

    fun updateWarning80Enabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateWarning80Enabled(enabled) }
    }

    fun updateExceededAlertEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateExceededAlertEnabled(enabled) }
    }

    fun updateNoBudgetReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateNoBudgetReminderEnabled(enabled) }
    }

    fun resetSettings() {
        viewModelScope.launch { userPreferencesRepository.resetPreferences() }
    }

    fun clearAllData() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            budgetRepository.deleteAllBudgets()
        }
    }
}

class SettingsViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                userPreferencesRepository,
                transactionRepository,
                budgetRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
