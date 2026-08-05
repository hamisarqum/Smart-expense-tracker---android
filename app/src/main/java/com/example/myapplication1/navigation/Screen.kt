package com.example.myapplication1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transactions : Screen("transactions", "Transactions", Icons.Default.List)
    object Reports : Screen("reports", "Reports", Icons.Default.DateRange)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Budget : Screen("budget", "Budget")
    object AddTransaction : Screen("add_transaction?transactionId={transactionId}", "Add Transaction") {
        fun createRoute(transactionId: Long? = null) = "add_transaction?transactionId=${transactionId ?: -1L}"
    }
}
