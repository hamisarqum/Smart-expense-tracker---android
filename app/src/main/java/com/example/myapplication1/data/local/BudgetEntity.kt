package com.example.myapplication1.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [Index(value = ["monthKey"], unique = true)]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthKey: String, // YYYY-MM
    val amount: Double,
    val createdAt: Long,
    val updatedAt: Long
)
