package com.example.myapplication1.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM transactions WHERE type = 'EXPENSE' GROUP BY category")
    fun getExpensesByCategory(): Flow<List<CategorySum>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getTransactionsInRange(start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date >= :start AND date < :end")
    fun getIncomeTotalInRange(start: Long, end: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :start AND date < :end")
    fun getExpenseTotalInRange(start: Long, end: Long): Flow<Double?>

    @Query("SELECT category, SUM(amount) as totalAmount FROM transactions WHERE type = :type AND date >= :start AND date < :end GROUP BY category")
    fun getCategoryTotalsInRange(start: Long, end: Long, type: String): Flow<List<CategorySum>>

    @Query("SELECT date, SUM(amount) as totalAmount FROM transactions WHERE type = 'EXPENSE' AND date >= :start AND date < :end GROUP BY date ORDER BY date ASC")
    fun getDailyExpenseTotalsInRange(start: Long, end: Long): Flow<List<TimeTotal>>

    @Query("SELECT strftime('%Y-%m', datetime(date / 1000, 'unixepoch')) as period, type, SUM(amount) as totalAmount FROM transactions WHERE date >= :start AND date < :end GROUP BY period, type")
    fun getMonthlyTotalsInRange(start: Long, end: Long): Flow<List<TimeTypeTotal>>

    @Query("SELECT MAX(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :start AND date < :end")
    fun getHighestExpenseInRange(start: Long, end: Long): Flow<Double?>

    @Query("SELECT AVG(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :start AND date < :end")
    fun getAverageExpenseInRange(start: Long, end: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM transactions WHERE date >= :start AND date < :end")
    fun getTransactionCountInRange(start: Long, end: Long): Flow<Int>
}

data class CategorySum(
    val category: String,
    val totalAmount: Double
)

data class TimeTotal(
    val date: Long,
    val totalAmount: Double
)

data class TimeTypeTotal(
    val period: String,
    val type: String,
    val totalAmount: Double
)
