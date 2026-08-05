package com.example.myapplication1.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.myapplication1.data.local.TransactionEntity
import com.example.myapplication1.utils.FormatUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {
    suspend fun generateReport(
        context: Context,
        userName: String,
        periodLabel: String,
        transactions: List<TransactionEntity>,
        currencyCode: String,
        totals: Map<String, Double>
    ): File? = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val fileName = "SmartExpenseTracker_Report_$timestamp.pdf"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            
            val file = File(exportDir, fileName)
            val document = PdfDocument()
            
            // Basic Page Setup
            val pageWidth = 595 // A4 size in points
            val pageHeight = 842
            var pageNumber = 1
            var yPosition = 50f
            
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas
            
            val paint = Paint()
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
            val headerPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
            }
            val textPaint = Paint().apply {
                textSize = 12f
            }

            // Title
            canvas.drawText("Smart Expense Tracker Report", 50f, yPosition, titlePaint)
            yPosition += 40f
            
            // Subtitle
            canvas.drawText("User: $userName", 50f, yPosition, textPaint)
            yPosition += 20f
            canvas.drawText("Period: $periodLabel", 50f, yPosition, textPaint)
            yPosition += 20f
            canvas.drawText("Generated: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}", 50f, yPosition, textPaint)
            yPosition += 40f
            
            // Summary Section
            canvas.drawText("Summary", 50f, yPosition, headerPaint)
            yPosition += 25f
            
            totals.forEach { (label, value) ->
                canvas.drawText("$label: ${FormatUtils.formatCurrency(value, currencyCode)}", 50f, yPosition, textPaint)
                yPosition += 20f
            }
            
            yPosition += 30f
            
            // Transactions Table
            canvas.drawText("Recent Transactions", 50f, yPosition, headerPaint)
            yPosition += 25f
            
            transactions.take(20).forEach { t ->
                if (yPosition > pageHeight - 50) {
                    document.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = 50f
                }
                
                val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(t.date))
                val line = "$dateStr | ${t.title} | ${t.type} | ${FormatUtils.formatCurrency(t.amount, currencyCode)}"
                canvas.drawText(line, 50f, yPosition, textPaint)
                yPosition += 20f
            }
            
            document.finishPage(page)
            
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
