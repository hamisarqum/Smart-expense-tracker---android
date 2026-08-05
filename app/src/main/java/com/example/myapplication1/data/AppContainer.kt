package com.example.myapplication1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication1.data.local.AppDatabase
import com.example.myapplication1.data.preferences.UserPreferencesRepository
import com.example.myapplication1.data.repository.BudgetRepository
import com.example.myapplication1.data.repository.ReportsRepository
import com.example.myapplication1.data.repository.TransactionRepository

private const val LAYOUT_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCES_NAME
)

interface AppContainer {
    val transactionRepository: TransactionRepository
    val budgetRepository: BudgetRepository
    val reportsRepository: ReportsRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(AppDatabase.getDatabase(context).transactionDao())
    }

    override val budgetRepository: BudgetRepository by lazy {
        BudgetRepository(
            AppDatabase.getDatabase(context).budgetDao(),
            AppDatabase.getDatabase(context).transactionDao()
        )
    }

    override val reportsRepository: ReportsRepository by lazy {
        ReportsRepository(
            AppDatabase.getDatabase(context).transactionDao(),
            AppDatabase.getDatabase(context).budgetDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
