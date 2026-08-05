package com.example.myapplication1

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication1.data.AppContainer
import com.example.myapplication1.data.AppDataContainer
import com.example.myapplication1.notifications.BudgetNotificationWorker
import java.util.concurrent.TimeUnit

class ExpenseTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        scheduleBudgetChecks()
    }

    private fun scheduleBudgetChecks() {
        val workRequest = PeriodicWorkRequestBuilder<BudgetNotificationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "budget_check_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
