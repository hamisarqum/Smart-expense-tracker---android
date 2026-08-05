package com.example.myapplication1.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val monthKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    private val displayFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM")

    fun getCurrentMonthKey(): String {
        return YearMonth.now().format(monthKeyFormatter)
    }

    fun getMonthKey(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(monthKeyFormatter)
    }

    fun getMonthStartTimestamp(monthKey: String): Long {
        val yearMonth = YearMonth.parse(monthKey, monthKeyFormatter)
        return yearMonth.atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun getMonthEndTimestamp(monthKey: String): Long {
        val yearMonth = YearMonth.parse(monthKey, monthKeyFormatter)
        return yearMonth.plusMonths(1)
            .atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun formatMonthDisplay(monthKey: String): String {
        val yearMonth = YearMonth.parse(monthKey, monthKeyFormatter)
        return yearMonth.format(displayFormatter)
    }

    fun getPeriodRange(period: ReportPeriod): Pair<Long, Long> {
        val now = LocalDate.now()
        val start = when (period) {
            ReportPeriod.CURRENT_MONTH -> now.withDayOfMonth(1)
            ReportPeriod.PREVIOUS_MONTH -> now.minusMonths(1).withDayOfMonth(1)
            ReportPeriod.LAST_3_MONTHS -> now.minusMonths(2).withDayOfMonth(1)
            ReportPeriod.LAST_6_MONTHS -> now.minusMonths(5).withDayOfMonth(1)
            ReportPeriod.CURRENT_YEAR -> now.withDayOfYear(1)
            ReportPeriod.CUSTOM -> now // Placeholder for custom logic
        }
        
        val end = when (period) {
            ReportPeriod.CURRENT_MONTH -> now.plusMonths(1).withDayOfMonth(1)
            ReportPeriod.PREVIOUS_MONTH -> now.withDayOfMonth(1)
            else -> now.plusDays(1) // Include today
        }

        return Pair(
            start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    fun getPreviousPeriodRange(start: Long, end: Long): Pair<Long, Long> {
        val duration = end - start
        return Pair(start - duration, start)
    }

    fun formatShortDate(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(shortDateFormatter)
    }

    fun startOfDay(timestamp: Long): Long {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}

enum class ReportPeriod(val label: String) {
    CURRENT_MONTH("Current Month"),
    PREVIOUS_MONTH("Previous Month"),
    LAST_3_MONTHS("Last 3 Months"),
    LAST_6_MONTHS("Last 6 Months"),
    CURRENT_YEAR("Current Year"),
    CUSTOM("Custom Range")
}
