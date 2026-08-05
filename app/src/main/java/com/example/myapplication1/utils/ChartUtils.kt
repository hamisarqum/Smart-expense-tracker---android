package com.example.myapplication1.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChartData(
    val label: String,
    val value: Double,
    val color: Color
)

@Composable
fun DonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    thickness: Float = 40f
) {
    val total = data.sumOf { it.value }.takeIf { it > 0 } ?: 1.0
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            data.forEach { item ->
                val sweepAngle = (item.value / total * 360).toFloat()
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = thickness, cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Total", style = MaterialTheme.typography.labelSmall)
            Text(
                text = FormatUtils.formatCurrency(data.sumOf { it.value }),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BarChart(
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    val maxVal = maxOf(income, expense).takeIf { it > 0 } ?: 1.0
    val incomeHeight = (income / maxVal).toFloat()
    val expenseHeight = (expense / maxVal).toFloat()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = FormatUtils.formatCurrency(income), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.width(40.dp).fillMaxHeight(incomeHeight)) {
                drawRect(Color(0xFF4CAF50))
            }
            Text(text = "Income", style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = FormatUtils.formatCurrency(expense), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.width(40.dp).fillMaxHeight(expenseHeight)) {
                drawRect(Color(0xFFF44336))
            }
            Text(text = "Expense", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun LineChart(
    points: List<Double>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    val maxVal = points.maxOrNull()?.takeIf { it > 0 } ?: 1.0
    val minVal = 0.0

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (points.size - 1).coerceAtLeast(1)

        val pathPoints = points.mapIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minVal) / (maxVal - minVal) * height).toFloat()
            Offset(x, y)
        }

        for (i in 0 until pathPoints.size - 1) {
            drawLine(
                color = Color.Blue,
                start = pathPoints[i],
                end = pathPoints[i + 1],
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        
        pathPoints.forEach { point ->
            drawCircle(color = Color.Blue, radius = 6f, center = point)
        }
    }
}
