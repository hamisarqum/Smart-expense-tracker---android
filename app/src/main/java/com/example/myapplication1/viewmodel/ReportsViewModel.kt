package com.example.myapplication1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.data.local.CategorySum
import com.example.myapplication1.data.local.TransactionEntity
import com.example.myapplication1.data.repository.ReportsRepository
import com.example.myapplication1.utils.DateUtils
import com.example.myapplication1.utils.ReportPeriod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

data class ReportsUiState(
    val selectedPeriod: ReportPeriod = ReportPeriod.CURRENT_MONTH,
    val customStart: Long? = null,
    val customEnd: Long? = null,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netSavings: Double = 0.0,
    val savingsRate: Double = 0.0,
    val averageExpense: Double = 0.0,
    val highestExpense: Double = 0.0,
    val transactionCount: Int = 0,
    val categoryTotals: List<CategorySum> = emptyList(),
    val dailyExpenseTrend: List<Double> = emptyList(),
    val previousPeriodIncome: Double = 0.0,
    val previousPeriodExpenses: Double = 0.0,
    val incomeChange: Double = 0.0,
    val expenseChange: Double = 0.0,
    val loading: Boolean = false,
    val insights: List<String> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList()
)

class ReportsViewModel(private val repository: ReportsRepository) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.CURRENT_MONTH)
    private val _customRange = MutableStateFlow<Pair<Long?, Long?>>(null to null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reportsUiState: StateFlow<ReportsUiState> = combine(
        _selectedPeriod,
        _customRange
    ) { period, custom ->
        Pair(period, custom)
    }.flatMapLatest { (period, custom) ->
        val range = if (period == ReportPeriod.CUSTOM && custom.first != null && custom.second != null) {
            Pair(custom.first!!, custom.second!!)
        } else {
            DateUtils.getPeriodRange(period)
        }
        
        val start = range.first
        val end = range.second
        val prevRange = DateUtils.getPreviousPeriodRange(start, end)
        
        val currentIncomeFlow = repository.getIncomeTotalStream(start, end)
        val currentExpenseFlow = repository.getExpenseTotalStream(start, end)
        val highestFlow = repository.getHighestExpenseStream(start, end)
        val avgFlow = repository.getAverageExpenseStream(start, end)
        val countFlow = repository.getTransactionCountStream(start, end)
        val categoriesFlow = repository.getCategoryTotalsStream(start, end, "EXPENSE")
        val dailyFlow = repository.getDailyExpenseTotalsStream(start, end)
        val prevIncomeFlow = repository.getIncomeTotalStream(prevRange.first, prevRange.second)
        val prevExpenseFlow = repository.getExpenseTotalStream(prevRange.first, prevRange.second)
        val txsFlow = repository.getTransactionsInRangeStream(start, end)

        combine(
            currentIncomeFlow, currentExpenseFlow, highestFlow, avgFlow, countFlow,
            categoriesFlow, dailyFlow, prevIncomeFlow, prevExpenseFlow, txsFlow
        ) { args ->
            val currentIncome = args[0] as Double
            val currentExpense = args[1] as Double
            val highest = args[2] as Double
            val avg = args[3] as Double
            val count = args[4] as Int
            val categories = args[5] as List<CategorySum>
            val daily = args[6] as List<com.example.myapplication1.data.local.TimeTotal>
            val prevIncome = args[7] as Double
            val prevExpense = args[8] as Double
            val txs = args[9] as List<TransactionEntity>

            val savings = currentIncome - currentExpense
            val savingsRate = if (currentIncome > 0) (savings / currentIncome) * 100 else 0.0
            
            val incChange = if (prevIncome > 0) ((currentIncome - prevIncome) / prevIncome) * 100 else 0.0
            val expChange = if (prevExpense > 0) ((currentExpense - prevExpense) / prevExpense) * 100 else 0.0
            
            val insights = mutableListOf<String>()
            if (currentExpense > prevExpense && prevExpense > 0) {
                insights.add("Spending increased by ${String.format(Locale.getDefault(), "%.1f", expChange)}% compared to last period.")
            }
            if (categories.isNotEmpty()) {
                insights.add("${categories.maxBy { it.totalAmount }.category} is your top expense category.")
            }
            if (savingsRate < 10) {
                insights.add("Your savings rate is below 10%. Consider reviewing non-essential costs.")
            }

            ReportsUiState(
                selectedPeriod = period,
                customStart = custom.first,
                customEnd = custom.second,
                totalIncome = currentIncome,
                totalExpenses = currentExpense,
                netSavings = savings,
                savingsRate = savingsRate,
                averageExpense = avg,
                highestExpense = highest,
                transactionCount = count,
                categoryTotals = categories,
                dailyExpenseTrend = daily.map { it.totalAmount },
                previousPeriodIncome = prevIncome,
                previousPeriodExpenses = prevExpense,
                incomeChange = incChange,
                expenseChange = expChange,
                insights = insights.take(3),
                transactions = txs
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportsUiState(loading = true))

    fun updatePeriod(period: ReportPeriod) {
        _selectedPeriod.value = period
    }

    fun updateCustomRange(start: Long, end: Long) {
        _customRange.value = start to end
        _selectedPeriod.value = ReportPeriod.CUSTOM
    }
}

class ReportsViewModelFactory(private val repository: ReportsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
