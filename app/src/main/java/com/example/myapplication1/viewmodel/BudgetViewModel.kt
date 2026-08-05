package com.example.myapplication1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.data.local.BudgetEntity
import com.example.myapplication1.data.local.CategorySum
import com.example.myapplication1.data.repository.BudgetRepository
import com.example.myapplication1.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class BudgetStatus {
    NO_BUDGET, SAFE, WARNING, EXCEEDED
}

data class BudgetUiState(
    val monthKey: String = DateUtils.getCurrentMonthKey(),
    val budgetAmount: Double = 0.0,
    val spentAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val progress: Float = 0f,
    val status: BudgetStatus = BudgetStatus.NO_BUDGET,
    val hasBudget: Boolean = false
)

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val currentMonthKey = DateUtils.getCurrentMonthKey()

    val budgetUiState: StateFlow<BudgetUiState> = combine(
        repository.getBudgetStream(currentMonthKey),
        repository.getMonthlyExpenseTotalStream(currentMonthKey)
    ) { budget, spent ->
        if (budget == null) {
            BudgetUiState(spentAmount = spent, status = BudgetStatus.NO_BUDGET, hasBudget = false)
        } else {
            val remaining = budget.amount - spent
            val progress = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f
            val status = when {
                progress >= 1.0f -> BudgetStatus.EXCEEDED
                progress >= 0.8f -> BudgetStatus.WARNING
                else -> BudgetStatus.SAFE
            }
            BudgetUiState(
                monthKey = currentMonthKey,
                budgetAmount = budget.amount,
                spentAmount = spent,
                remainingAmount = remaining,
                progress = progress,
                status = status,
                hasBudget = true
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetUiState())

    val expensesByCategory: StateFlow<List<CategorySum>> = repository.getMonthlyExpensesByCategoryStream(currentMonthKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val existing = repository.getBudgetStream(currentMonthKey).first()
            val budget = BudgetEntity(
                id = existing?.id ?: 0L,
                monthKey = currentMonthKey,
                amount = amount,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
            repository.insertOrUpdateBudget(budget)
        }
    }

    fun deleteBudget() {
        viewModelScope.launch {
            val existing = repository.getBudgetStream(currentMonthKey).first()
            existing?.let { repository.deleteBudget(it) }
        }
    }
}

class BudgetViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
