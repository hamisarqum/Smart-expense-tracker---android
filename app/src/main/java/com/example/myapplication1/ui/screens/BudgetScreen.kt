package com.example.myapplication1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication1.R
import com.example.myapplication1.ui.components.AddBudgetDialog
import com.example.myapplication1.ui.components.CategorySummaryItem
import com.example.myapplication1.ui.theme.Dimensions
import com.example.myapplication1.utils.DateUtils
import com.example.myapplication1.utils.FormatUtils
import com.example.myapplication1.viewmodel.BudgetStatus
import com.example.myapplication1.viewmodel.BudgetViewModel
import java.util.Locale

@Composable
fun BudgetScreen(viewModel: BudgetViewModel, currencyCode: String) {
    val uiState by viewModel.budgetUiState.collectAsStateWithLifecycle()
    val expensesByCategory by viewModel.expensesByCategory.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.PaddingMedium)
    ) {
        item {
            Text(
                text = DateUtils.formatMonthDisplay(uiState.monthKey),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = Dimensions.PaddingMedium)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(Dimensions.PaddingLarge)) {
                    Text(text = stringResource(R.string.monthly_budget), style = MaterialTheme.typography.titleMedium)
                    if (uiState.hasBudget) {
                        Text(
                            text = FormatUtils.formatCurrency(uiState.budgetAmount, currencyCode),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
                        
                        LinearProgressIndicator(
                            progress = { uiState.progress.coerceAtMost(1.0f) },
                            modifier = Modifier.fillMaxWidth().height(12.dp),
                            strokeCap = StrokeCap.Round,
                            color = when (uiState.status) {
                                BudgetStatus.EXCEEDED -> MaterialTheme.colorScheme.error
                                BudgetStatus.WARNING -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = Dimensions.SpacingMedium),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${stringResource(R.string.expenses)}: ${FormatUtils.formatCurrency(uiState.spentAmount, currencyCode)}")
                            if (uiState.status == BudgetStatus.EXCEEDED) {
                                Text(
                                    text = stringResource(R.string.over_budget_by, FormatUtils.formatCurrency(uiState.spentAmount - uiState.budgetAmount, currencyCode)),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                Text(text = "${stringResource(R.string.remaining)}: ${FormatUtils.formatCurrency(uiState.remainingAmount, currencyCode)}")
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.no_budget_set),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.padding(top = Dimensions.PaddingMedium).align(Alignment.End)
                    ) {
                        Text(if (uiState.hasBudget) stringResource(R.string.edit_budget) else stringResource(R.string.set_budget))
                    }
                }
            }

            if (uiState.status == BudgetStatus.WARNING) {
                Card(
                    modifier = Modifier.padding(top = Dimensions.PaddingMedium).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Text(
                        text = "Warning: You have used ${String.format(Locale.getDefault(), "%.0f", uiState.progress * 100)}% of your budget.",
                        modifier = Modifier.padding(Dimensions.PaddingMedium),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = stringResource(R.string.spending_by_category),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingSmall)
            )
        }

        if (expensesByCategory.isEmpty()) {
            item {
                Text(
                    text = "No expenses recorded this month",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = Dimensions.PaddingMedium)
                )
            }
        } else {
            items(expensesByCategory) { categorySum ->
                CategorySummaryItem(
                    category = categorySum.category,
                    amount = FormatUtils.formatCurrency(categorySum.totalAmount, currencyCode),
                    percentage = if (uiState.hasBudget && uiState.budgetAmount > 0) 
                        (categorySum.totalAmount / uiState.budgetAmount).toFloat()
                    else if (uiState.spentAmount > 0)
                        (categorySum.totalAmount / uiState.spentAmount).toFloat()
                    else 0f
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showEditDialog) {
        AddBudgetDialog(
            monthDisplay = DateUtils.formatMonthDisplay(uiState.monthKey),
            initialAmount = if (uiState.hasBudget) uiState.budgetAmount else null,
            onDismiss = { showEditDialog = false },
            onConfirm = {
                viewModel.setBudget(it)
                showEditDialog = false
            },
            onDelete = {
                viewModel.deleteBudget()
            }
        )
    }
}
