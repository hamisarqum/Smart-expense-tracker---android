package com.example.myapplication1.model

import java.util.Date

object SampleData {
    val transactions = listOf(
        Transaction("1", "Grocery Shopping", 50.0, TransactionType.EXPENSE, Category.FOOD, Date(), "Weekly groceries"),
        Transaction("2", "Monthly Salary", 3000.0, TransactionType.INCOME, Category.SALARY, Date(), "Main income"),
        Transaction("3", "Electric Bill", 120.0, TransactionType.EXPENSE, Category.BILLS, Date(), "Home utilities"),
        Transaction("4", "Movie Tickets", 30.0, TransactionType.EXPENSE, Category.ENTERTAINMENT, Date(), "Avengers movie"),
        Transaction("5", "Bus Pass", 40.0, TransactionType.EXPENSE, Category.TRANSPORT, Date(), "Public transport"),
        Transaction("6", "New Shoes", 80.0, TransactionType.EXPENSE, Category.SHOPPING, Date(), "Nike run"),
        Transaction("7", "Gym Membership", 60.0, TransactionType.EXPENSE, Category.HEALTH, Date(), "Monthly sub")
    )

    val balance = 2620.0
    val income = 3000.0
    val expenses = 380.0
    val budgetProgress = 0.4f
}
