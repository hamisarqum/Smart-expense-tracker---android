package com.example.myapplication1.export

import android.content.Context
import com.example.myapplication1.data.local.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    suspend fun exportTransactions(
        context: Context,
        transactions: List<TransactionEntity>,
        currencyCode: String
    ): File? = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val fileName = "SmartExpenseTracker_Transactions_$timestamp.csv"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            
            val file = File(exportDir, fileName)
            val writer = FileWriter(file)
            
            // Header
            writer.append("ID,Date,Type,Title,Category,Amount,CurrencyCode,Notes,CreatedAt\n")
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            transactions.forEach { t ->
                writer.append("${t.id},")
                writer.append("${dateFormat.format(Date(t.date))},")
                writer.append("${t.type},")
                writer.append("\"${escape(t.title)}\",")
                writer.append("\"${escape(t.category)}\",")
                writer.append("${t.amount},")
                writer.append("$currencyCode,")
                writer.append("\"${escape(t.notes)}\",")
                writer.append("${dateFormat.format(Date(t.createdAt))}\n")
            }
            
            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun escape(s: String): String {
        return s.replace("\"", "\"\"").replace("\n", " ")
    }
}
