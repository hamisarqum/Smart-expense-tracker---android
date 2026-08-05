package com.example.myapplication1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.data.local.TransactionEntity
import com.example.myapplication1.export.CsvExporter
import com.example.myapplication1.export.PdfReportGenerator
import com.example.myapplication1.utils.FileShareUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ExportUiState(
    val isExporting: Boolean = false,
    val latestFile: File? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class ExportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState = _uiState.asStateFlow()

    fun exportCsv(context: Context, transactions: List<TransactionEntity>, currencyCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, error = null, successMessage = null)
            val file = CsvExporter.exportTransactions(context, transactions, currencyCode)
            if (file != null) {
                _uiState.value = _uiState.value.copy(isExporting = false, latestFile = file, successMessage = "CSV exported successfully")
            } else {
                _uiState.value = _uiState.value.copy(isExporting = false, error = "Failed to export CSV")
            }
        }
    }

    fun exportPdf(
        context: Context,
        userName: String,
        periodLabel: String,
        transactions: List<TransactionEntity>,
        currencyCode: String,
        totals: Map<String, Double>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, error = null, successMessage = null)
            val file = PdfReportGenerator.generateReport(context, userName, periodLabel, transactions, currencyCode, totals)
            if (file != null) {
                _uiState.value = _uiState.value.copy(isExporting = false, latestFile = file, successMessage = "PDF report generated successfully")
            } else {
                _uiState.value = _uiState.value.copy(isExporting = false, error = "Failed to generate PDF")
            }
        }
    }

    fun shareLatestFile(context: Context) {
        val file = _uiState.value.latestFile
        if (file != null) {
            val mimeType = if (file.extension == "csv") "text/csv" else "application/pdf"
            FileShareUtils.shareFile(context, file, mimeType)
        }
    }

    fun clearEvents() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
