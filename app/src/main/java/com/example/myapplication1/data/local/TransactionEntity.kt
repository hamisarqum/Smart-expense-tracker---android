package com.example.myapplication1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // INCOME, EXPENSE
    val category: String,
    val date: Long,
    val notes: String,
    val createdAt: Long
)
