package com.example.myapplication1.data.repository

import com.example.myapplication1.data.local.BudgetDao
import com.example.myapplication1.data.local.BudgetEntity
import com.example.myapplication1.data.local.TransactionDao
import com.example.myapplication1.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    fun getBudgetStream(monthKey: String): Flow<BudgetEntity?> = budgetDao.observeBudgetByMonth(monthKey)

    fun getMonthlyExpenseTotalStream(monthKey: String): Flow<Double> {
        val start = DateUtils.getMonthStartTimestamp(monthKey)
        val end = DateUtils.getMonthEndTimestamp(monthKey)
        return transactionDao.getExpenseTotalInRange(start, end).map { it ?: 0.0 }
    }

    fun getMonthlyExpensesByCategoryStream(monthKey: String) = transactionDao.getCategoryTotalsInRange(
        DateUtils.getMonthStartTimestamp(monthKey),
        DateUtils.getMonthEndTimestamp(monthKey),
        "EXPENSE"
    )

    suspend fun insertOrUpdateBudget(budget: BudgetEntity) = budgetDao.insertOrUpdate(budget)

    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.delete(budget)

    suspend fun deleteAllBudgets() = budgetDao.deleteAll()
}
