package com.example.myapplication1.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceCalculationsTest {

    @Test
    fun `savings rate calculation is correct`() {
        val income = 1000.0
        val expenses = 400.0
        val savings = income - expenses
        val rate = (savings / income) * 100
        assertEquals(60.0, rate, 0.001)
    }

    @Test
    fun `savings rate with zero income returns zero`() {
        val income = 0.0
        val expenses = 400.0
        val savings = income - expenses
        val rate = if (income > 0) (savings / income) * 100 else 0.0
        assertEquals(0.0, rate, 0.001)
    }

    @Test
    fun `percentage change calculation is correct`() {
        val current = 120.0
        val previous = 100.0
        val change = ((current - previous) / previous) * 100
        assertEquals(20.0, change, 0.001)
    }

    @Test
    fun `percentage change with zero previous returns zero`() {
        val current = 120.0
        val previous = 0.0
        val change = if (previous > 0) ((current - previous) / previous) * 100 else 0.0
        assertEquals(0.0, change, 0.001)
    }
}
