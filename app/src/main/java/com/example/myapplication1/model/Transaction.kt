package com.example.myapplication1.model

import java.util.Date

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val date: Date,
    val notes: String = ""
)
