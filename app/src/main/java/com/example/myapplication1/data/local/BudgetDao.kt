package com.example.myapplication1.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey")
    fun observeBudgetByMonth(monthKey: String): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey")
    suspend fun getBudgetByMonth(monthKey: String): BudgetEntity?

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()

    @Query("SELECT * FROM budgets ORDER BY monthKey DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>
}
