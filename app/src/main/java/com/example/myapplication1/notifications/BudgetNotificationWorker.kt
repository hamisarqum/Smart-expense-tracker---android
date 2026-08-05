package com.example.myapplication1.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication1.ExpenseTrackerApplication
import com.example.myapplication1.R
import com.example.myapplication1.utils.DateUtils
import com.example.myapplication1.utils.FormatUtils
import kotlinx.coroutines.flow.first

class BudgetNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val CHANNEL_ID = "budget_alerts"
    private val WARNING_NOTIFICATION_ID = 101
    private val EXCEEDED_NOTIFICATION_ID = 102

    override suspend fun doWork(): Result {
        val application = applicationContext as ExpenseTrackerApplication
        val prefsRepo = application.container.userPreferencesRepository
        val budgetRepo = application.container.budgetRepository
        
        val prefs = prefsRepo.userPreferencesStream.first()
        if (!prefs.budgetNotificationsEnabled) return Result.success()

        val monthKey = DateUtils.getCurrentMonthKey()
        val budget = budgetRepo.getBudgetStream(monthKey).first() ?: return Result.success()
        val spent = budgetRepo.getMonthlyExpenseTotalStream(monthKey).first()

        if (budget.amount <= 0) return Result.success()
        val progress = spent / budget.amount

        createNotificationChannel()

        if (progress >= 1.0 && prefs.exceededAlertEnabled && prefs.lastMonthWarned100 != monthKey) {
            sendNotification(
                EXCEEDED_NOTIFICATION_ID,
                "Budget Exceeded!",
                "You have spent ${FormatUtils.formatCurrency(spent, prefs.currencyCode)}, which exceeds your budget of ${FormatUtils.formatCurrency(budget.amount, prefs.currencyCode)}."
            )
            prefsRepo.updateLastMonthWarned100(monthKey)
        } else if (progress >= 0.8 && prefs.warning80Enabled && prefs.lastMonthWarned80 != monthKey) {
            sendNotification(
                WARNING_NOTIFICATION_ID,
                "Budget Warning",
                "You have used 80% of your monthly budget."
            )
            prefsRepo.updateLastMonthWarned80(monthKey)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alerts"
            val descriptionText = "Notifications for budget thresholds"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(id: Int, title: String, content: String) {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }
}
