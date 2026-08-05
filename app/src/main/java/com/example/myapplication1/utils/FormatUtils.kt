package com.example.myapplication1.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatCurrency(amount: Double, currencyCode: String = "GBP"): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "GB"))
            format.currency = Currency.getInstance(currencyCode)
            format.format(amount)
        } catch (e: Exception) {
            // Fallback to GBP if currency code is invalid
            val format = NumberFormat.getCurrencyInstance(Locale("en", "GB"))
            format.currency = Currency.getInstance("GBP")
            format.format(amount)
        }
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
}
