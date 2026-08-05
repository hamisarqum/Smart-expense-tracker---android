package com.example.myapplication1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication1.R
import com.example.myapplication1.data.preferences.UserPreferences
import com.example.myapplication1.ui.theme.Dimensions
import com.example.myapplication1.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    currentPrefs: UserPreferences,
    onShowSnackbar: (String) -> Unit
) {
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    val currencies = listOf("GBP", "USD", "EUR", "PKR", "AED", "SAR", "INR")
    val themes = listOf("SYSTEM", "LIGHT", "DARK")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.PaddingMedium)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = stringResource(R.string.profile), style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.SpacingMedium),
            onClick = { showNameDialog = true }
        ) {
            Column(modifier = Modifier.padding(Dimensions.PaddingMedium)) {
                Text(text = stringResource(R.string.user_name), style = MaterialTheme.typography.labelSmall)
                Text(text = currentPrefs.userName, style = MaterialTheme.typography.headlineSmall)
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        Text(text = stringResource(R.string.preferences), style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.SpacingMedium),
            onClick = { showCurrencyDialog = true }
        ) {
            Column(modifier = Modifier.padding(Dimensions.PaddingMedium)) {
                Text(text = stringResource(R.string.currency), style = MaterialTheme.typography.labelSmall)
                Text(text = currentPrefs.currencyCode, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        Text(text = stringResource(R.string.appearance), style = MaterialTheme.typography.titleMedium)

        Column(Modifier.selectableGroup()) {
            themes.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(Dimensions.ButtonHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (theme == currentPrefs.themeMode),
                        onClick = { viewModel.updateThemeMode(theme) }
                    )
                    Text(
                        text = theme.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = Dimensions.PaddingMedium)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        Text(text = stringResource(R.string.notifications), style = MaterialTheme.typography.titleMedium)
        
        ListItem(
            headlineContent = { Text("Budget Notifications") },
            supportingContent = { Text("Get alerts about your spending progress") },
            trailingContent = {
                Switch(
                    checked = currentPrefs.budgetNotificationsEnabled,
                    onCheckedChange = { viewModel.updateBudgetNotificationsEnabled(it) }
                )
            }
        )
        
        if (currentPrefs.budgetNotificationsEnabled) {
            ListItem(
                headlineContent = { Text("80% Spending Warning") },
                trailingContent = {
                    Checkbox(
                        checked = currentPrefs.warning80Enabled,
                        onCheckedChange = { viewModel.updateWarning80Enabled(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Budget Exceeded Alert") },
                trailingContent = {
                    Checkbox(
                        checked = currentPrefs.exceededAlertEnabled,
                        onCheckedChange = { viewModel.updateExceededAlertEnabled(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("No Budget Reminder") },
                supportingContent = { Text("Monthly reminder to set a budget") },
                trailingContent = {
                    Checkbox(
                        checked = currentPrefs.noBudgetReminderEnabled,
                        onCheckedChange = { viewModel.updateNoBudgetReminderEnabled(it) }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        
        Button(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth().height(Dimensions.ButtonHeight)
        ) {
            Text(stringResource(R.string.reset_settings))
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

        Button(
            onClick = { showClearDataDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().height(Dimensions.ButtonHeight)
        ) {
            Text(stringResource(R.string.clear_all))
        }

        Spacer(modifier = Modifier.height(Dimensions.PaddingExtraLarge))
        HorizontalDivider()
        Text(
            text = stringResource(R.string.about_app),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = Dimensions.PaddingMedium)
        )
        Text(
            text = stringResource(R.string.privacy_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.app_version, "1.0.0"), // Hardcoded for simplicity or use BuildConfig.VERSION_NAME
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(vertical = Dimensions.PaddingMedium)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showNameDialog) {
        var tempName by remember { mutableStateOf(currentPrefs.userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text(stringResource(R.string.edit) + " " + stringResource(R.string.user_name)) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempName.isNotBlank()) {
                        viewModel.updateUserName(tempName)
                        showNameDialog = false
                    }
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text(stringResource(R.string.currency)) },
            text = {
                Column {
                    currencies.forEach { code ->
                        TextButton(
                            onClick = {
                                viewModel.updateCurrency(code)
                                showCurrencyDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(code, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_settings)) },
            text = { Text("Reset preferences to defaults? Your data will not be deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetSettings()
                    showResetDialog = false
                    onShowSnackbar("Settings reset")
                }) { Text(stringResource(R.string.reset)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(stringResource(R.string.clear_all)) },
            text = { Text("Permanently delete ALL transactions and budgets? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                        onShowSnackbar("Data cleared")
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
