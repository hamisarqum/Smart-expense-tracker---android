package com.example.myapplication1.data.repository

import com.example.myapplication1.data.local.CategorySum
import com.example.myapplication1.data.local.TransactionDao
import com.example.myapplication1.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun getAllTransactionsStream(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun searchTransactionsStream(query: String): Flow<List<TransactionEntity>> = transactionDao.searchTransactions(query)

    fun getTransactionsByTypeStream(type: String): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByType(type)

    fun getTotalIncomeStream(): Flow<Double?> = transactionDao.getTotalIncome()

    fun getTotalExpensesStream(): Flow<Double?> = transactionDao.getTotalExpenses()

    fun getRecentTransactionsStream(limit: Int): Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(limit)

    fun getExpensesByCategoryStream(): Flow<List<CategorySum>> = transactionDao.getExpensesByCategory()

    suspend fun getTransactionById(id: Long): TransactionEntity? = transactionDao.getById(id)

    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.update(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.delete(transaction)

    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteById(id)

    suspend fun deleteAllTransactions() = transactionDao.deleteAll()
}
