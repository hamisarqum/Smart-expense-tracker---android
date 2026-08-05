package com.example.myapplication1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication1.R
import com.example.myapplication1.data.local.TransactionEntity
import com.example.myapplication1.ui.components.TransactionItem
import com.example.myapplication1.ui.theme.Dimensions
import com.example.myapplication1.viewmodel.TransactionViewModel

@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel,
    currencyCode: String,
    onEditTransaction: (Long) -> Unit
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    
    var showDeleteDialog by remember { mutableStateOf<TransactionEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.PaddingMedium)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.PaddingSmall),
            placeholder = { Text(stringResource(R.string.search_transactions)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.PaddingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            FilterChip(
                selected = filterType == null,
                onClick = { viewModel.updateFilterType(null) },
                label = { Text(stringResource(R.string.filter_all)) }
            )
            FilterChip(
                selected = filterType == "INCOME",
                onClick = { viewModel.updateFilterType("INCOME") },
                label = { Text(stringResource(R.string.filter_income)) }
            )
            FilterChip(
                selected = filterType == "EXPENSE",
                onClick = { viewModel.updateFilterType("EXPENSE") },
                label = { Text(stringResource(R.string.filter_expense)) }
            )
        }

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_transactions_found), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        currencyCode = currencyCode,
                        onClick = { onEditTransaction(transaction.id) },
                        onDelete = { showDeleteDialog = transaction }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.confirm)) }, // Simplified for phase 5
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteTransaction(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
