package com.example.myapplication1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication1.R
import com.example.myapplication1.ui.components.CategorySummaryItem
import com.example.myapplication1.ui.components.SummaryCard
import com.example.myapplication1.ui.theme.Dimensions
import com.example.myapplication1.utils.*
import com.example.myapplication1.viewmodel.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel,
    exportViewModel: ExportViewModel = viewModel()
) {
    val uiState by reportsViewModel.reportsUiState.collectAsStateWithLifecycle()
    val userPrefs by settingsViewModel.userPreferences.collectAsStateWithLifecycle()
    val exportState by exportViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(exportState.successMessage) {
        exportState.successMessage?.let {
            exportViewModel.clearEvents()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.PaddingMedium)
    ) {
        item {
            PeriodFilterSection(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = { reportsViewModel.updatePeriod(it) },
                onCustomClick = { showDatePicker = true }
            )

            AdvancedSummarySection(uiState, userPrefs.currencyCode)

            Text(
                text = stringResource(R.string.spending_by_category),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingMedium)
            )
            
            if (uiState.categoryTotals.isNotEmpty()) {
                val categoryColors = listOf(
                    Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFF018786),
                    Color(0xFFB00020), Color(0xFFFFAB00), Color(0xFFFF6D00),
                    Color(0xFF2962FF), Color(0xFF00C853), Color(0xFFAA00FF)
                )
                DonutChart(
                    data = uiState.categoryTotals.mapIndexed { index, item -> 
                        ChartData(item.category, item.totalAmount, categoryColors[index % categoryColors.size]) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(Dimensions.PaddingMedium)
                )
            } else {
                Text(text = "No category data available", style = MaterialTheme.typography.bodyMedium)
            }

            Text(
                text = "Income vs Expenses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingMedium)
            )
            
            BarChart(
                income = uiState.totalIncome,
                expense = uiState.totalExpenses,
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(Dimensions.PaddingMedium)
            )

            Text(
                text = stringResource(R.string.spending_trend),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingMedium)
            )
            
            if (uiState.dailyExpenseTrend.isNotEmpty()) {
                LineChart(
                    points = uiState.dailyExpenseTrend,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(Dimensions.PaddingSmall)
                )
            } else {
                Text(text = "Not enough data for trend analysis", style = MaterialTheme.typography.bodyMedium)
            }

            ComparisonSection(uiState)

            InsightsSection(uiState.insights)

            ExportSection(
                isExporting = exportState.isExporting,
                hasLatestFile = exportState.latestFile != null,
                onExportCsv = { exportViewModel.exportCsv(context, uiState.transactions, userPrefs.currencyCode) },
                onExportPdf = { 
                    val totals = mapOf(
                        "Income" to uiState.totalIncome,
                        "Expenses" to uiState.totalExpenses,
                        "Savings" to uiState.netSavings,
                        "Savings Rate (%)" to uiState.savingsRate
                    )
                    val periodLabel = if (uiState.selectedPeriod == ReportPeriod.CUSTOM) {
                        "${FormatUtils.formatDate(uiState.customStart ?: 0L)} - ${FormatUtils.formatDate(uiState.customEnd ?: 0L)}"
                    } else uiState.selectedPeriod.label
                    exportViewModel.exportPdf(context, userPrefs.userName, periodLabel, uiState.transactions, userPrefs.currencyCode, totals) 
                },
                onShare = { exportViewModel.shareLatestFile(context) }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showDatePicker) {
        CustomRangeDialog(
            onDismiss = { showDatePicker = false },
            onRangeSelected = { start, end ->
                reportsViewModel.updateCustomRange(start, end)
                showDatePicker = false
            }
        )
    }
}

@Composable
fun PeriodFilterSection(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onCustomClick: () -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = ReportPeriod.entries.indexOf(selectedPeriod),
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        divider = {}
    ) {
        ReportPeriod.entries.forEach { period ->
            Tab(
                selected = selectedPeriod == period,
                onClick = { 
                    if (period == ReportPeriod.CUSTOM) onCustomClick() else onPeriodSelected(period)
                },
                text = { Text(period.label, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
fun AdvancedSummarySection(uiState: ReportsUiState, currencyCode: String) {
    Column(modifier = Modifier.padding(vertical = Dimensions.PaddingMedium)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
            Box(modifier = Modifier.weight(1f)) {
                SummaryCard(title = stringResource(R.string.income), amount = FormatUtils.formatCurrency(uiState.totalIncome, currencyCode))
            }
            Box(modifier = Modifier.weight(1f)) {
                SummaryCard(title = stringResource(R.string.expenses), amount = FormatUtils.formatCurrency(uiState.totalExpenses, currencyCode))
            }
        }
        SummaryCard(
            title = "Net Savings", 
            amount = FormatUtils.formatCurrency(uiState.netSavings, currencyCode),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = Dimensions.SpacingMedium), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoChip(label = "Savings Rate", value = "${String.format(Locale.getDefault(), "%.1f", uiState.savingsRate)}%")
            InfoChip(label = "Avg. Expense", value = FormatUtils.formatCurrency(uiState.averageExpense, currencyCode))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = Dimensions.SpacingMedium), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoChip(label = "Highest", value = FormatUtils.formatCurrency(uiState.highestExpense, currencyCode))
            InfoChip(label = "Transactions", value = uiState.transactionCount.toString())
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(vertical = Dimensions.SpacingSmall)
    ) {
        Column(modifier = Modifier.padding(horizontal = Dimensions.PaddingMedium, vertical = Dimensions.PaddingSmall)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComparisonSection(uiState: ReportsUiState) {
    Text(
        text = stringResource(R.string.period_comparison),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingMedium)
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimensions.PaddingMedium)) {
            ComparisonRow(stringResource(R.string.income), uiState.incomeChange)
            ComparisonRow(stringResource(R.string.expenses), uiState.expenseChange)
        }
    }
}

@Composable
fun ComparisonRow(label: String, change: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Dimensions.SpacingSmall), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label)
        val isNegativeGood = label == stringResource(R.string.expenses)
        val color = if (change > 0) {
            if (isNegativeGood) Color.Red else Color(0xFF4CAF50)
        } else if (change < 0) {
            if (isNegativeGood) Color(0xFF4CAF50) else Color.Red
        } else Color.Gray

        Text(
            text = "${if (change >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.1f", change)}%",
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InsightsSection(insights: List<String>) {
    if (insights.isEmpty()) return
    Text(
        text = stringResource(R.string.financial_insights),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingMedium)
    )
    insights.forEach { insight ->
        Card(
            modifier = Modifier.padding(vertical = Dimensions.SpacingSmall).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(text = insight, modifier = Modifier.padding(Dimensions.PaddingMedium), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ExportSection(
    isExporting: Boolean,
    hasLatestFile: Boolean,
    onExportCsv: () -> Unit,
    onExportPdf: () -> Unit,
    onShare: () -> Unit
) {
    Text(
        text = stringResource(R.string.export_share),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = Dimensions.PaddingLarge, bottom = Dimensions.PaddingMedium)
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
        Button(onClick = onExportCsv, enabled = !isExporting, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(Modifier.width(Dimensions.SpacingSmall))
            Text("CSV", fontSize = 12.sp)
        }
        Button(onClick = onExportPdf, enabled = !isExporting, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(Modifier.width(Dimensions.SpacingSmall))
            Text("PDF", fontSize = 12.sp)
        }
        OutlinedButton(onClick = onShare, enabled = hasLatestFile, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(Modifier.width(Dimensions.SpacingSmall))
            Text("Share", fontSize = 12.sp)
        }
    }
    if (isExporting) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = Dimensions.SpacingMedium))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRangeDialog(onDismiss: () -> Unit, onRangeSelected: (Long, Long) -> Unit) {
    val datePickerState = rememberDateRangePickerState()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val start = datePickerState.selectedStartDateMillis
                val end = datePickerState.selectedEndDateMillis
                if (start != null && end != null) {
                    onRangeSelected(start, end)
                }
            }) { Text(stringResource(R.string.save).replace("Save", "Apply")) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
        text = {
            Box(modifier = Modifier.height(450.dp)) {
                DateRangePicker(state = datePickerState)
            }
        }
    )
}

