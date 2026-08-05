package com.example.myapplication1.data.repository

import com.example.myapplication1.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReportsRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) {
    fun getIncomeTotalStream(start: Long, end: Long): Flow<Double> = 
        transactionDao.getIncomeTotalInRange(start, end).map { it ?: 0.0 }

    fun getExpenseTotalStream(start: Long, end: Long): Flow<Double> = 
        transactionDao.getExpenseTotalInRange(start, end).map { it ?: 0.0 }

    fun getTransactionsInRangeStream(start: Long, end: Long): Flow<List<TransactionEntity>> = 
        transactionDao.getTransactionsInRange(start, end)

    fun getCategoryTotalsStream(start: Long, end: Long, type: String): Flow<List<CategorySum>> = 
        transactionDao.getCategoryTotalsInRange(start, end, type)

    fun getDailyExpenseTotalsStream(start: Long, end: Long): Flow<List<TimeTotal>> = 
        transactionDao.getDailyExpenseTotalsInRange(start, end)

    fun getMonthlyTotalsStream(start: Long, end: Long): Flow<List<TimeTypeTotal>> = 
        transactionDao.getMonthlyTotalsInRange(start, end)

    fun getHighestExpenseStream(start: Long, end: Long): Flow<Double> = 
        transactionDao.getHighestExpenseInRange(start, end).map { it ?: 0.0 }

    fun getAverageExpenseStream(start: Long, end: Long): Flow<Double> = 
        transactionDao.getAverageExpenseInRange(start, end).map { it ?: 0.0 }

    fun getTransactionCountStream(start: Long, end: Long): Flow<Int> = 
        transactionDao.getTransactionCountInRange(start, end)

    fun getBudgetStream(monthKey: String): Flow<BudgetEntity?> = 
        budgetDao.observeBudgetByMonth(monthKey)
}
