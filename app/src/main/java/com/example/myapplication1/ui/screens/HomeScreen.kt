package com.example.myapplication1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication1.R
import com.example.myapplication1.ui.components.SummaryCard
import com.example.myapplication1.ui.components.TransactionItem
import com.example.myapplication1.ui.theme.Dimensions
import com.example.myapplication1.utils.FormatUtils
import com.example.myapplication1.viewmodel.BudgetStatus
import com.example.myapplication1.viewmodel.BudgetViewModel
import com.example.myapplication1.viewmodel.TransactionViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: TransactionViewModel,
    budgetViewModel: BudgetViewModel,
    currencyCode: String,
    userName: String,
    onViewBudget: () -> Unit
) {
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val income by viewModel.totalIncome.collectAsStateWithLifecycle()
    val expenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val budgetState by budgetViewModel.budgetUiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.PaddingMedium)
    ) {
        item {
            Text(
                text = stringResource(R.string.welcome_back, userName),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = Dimensions.PaddingSmall)
            )
            SummaryCard(
                title = stringResource(R.string.current_balance),
                amount = FormatUtils.formatCurrency(balance, currencyCode),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SummaryCard(title = stringResource(R.string.income), amount = FormatUtils.formatCurrency(income, currencyCode))
                }
                Box(modifier = Modifier.weight(1f)) {
                    SummaryCard(title = stringResource(R.string.expenses), amount = FormatUtils.formatCurrency(expenses, currencyCode))
                }
            }
            
            // Monthly Budget Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.PaddingSmall)
                    .clickable(onClick = onViewBudget),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(Dimensions.PaddingMedium)) {
                    Text(text = stringResource(R.string.budget_progress), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                    
                    if (budgetState.hasBudget) {
                        LinearProgressIndicator(
                            progress = { budgetState.progress.coerceAtMost(1.0f) },
                            modifier = Modifier.fillMaxWidth().height(Dimensions.SpacingMedium),
                            strokeCap = StrokeCap.Round,
                            color = when (budgetState.status) {
                                BudgetStatus.EXCEEDED -> MaterialTheme.colorScheme.error
                                BudgetStatus.WARNING -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = Dimensions.SpacingSmall),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.0f", budgetState.progress * 100)}% ${stringResource(R.string.used)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = if (budgetState.status == BudgetStatus.EXCEEDED) 
                                    stringResource(R.string.title_budget).replace("Budget", "Exceeded") // Simplified
                                    else "${FormatUtils.formatCurrency(budgetState.remainingAmount, currencyCode)} ${stringResource(R.string.remaining)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (budgetState.status == BudgetStatus.EXCEEDED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.no_budget_set),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.recent_transactions),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingSmall)
            )
        }
        
        if (recentTransactions.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_recent_transactions),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = Dimensions.PaddingMedium)
                )
            }
        } else {
            items(recentTransactions, key = { it.id }) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    currencyCode = currencyCode
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
