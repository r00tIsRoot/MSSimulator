package com.mssimulator.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mssimulator.data.formatAxisLabel
import com.mssimulator.model.DamageSnapshot

@OptIn(ExperimentalTextApi::class)
@Composable
fun DamageLineChart(
    snapshots: List<DamageSnapshot>,
    modifier: Modifier = Modifier,
) {
    if (snapshots.size < 2) return

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryFill = primaryColor.copy(alpha = 0.12f)

    val textMeasurer = rememberTextMeasurer()
    val axisTextStyle = TextStyle(
        fontSize = 10.sp,
        color = onSurfaceColor,
    )

    Canvas(modifier = modifier.padding(4.dp)) {
        val chartWidth = size.width
        val chartHeight = size.height
        val paddingLeft = 56f
        val paddingBottom = 36f
        val paddingTop = 16f
        val paddingRight = 16f
        val plotWidth = chartWidth - paddingLeft - paddingRight
        val plotHeight = chartHeight - paddingTop - paddingBottom

        if (plotWidth <= 0f || plotHeight <= 0f) return@Canvas

        val maxDamage = snapshots.maxOf { it.cumulativeDamage }.coerceAtLeast(1.0)
        val maxSecond = snapshots.last().second.coerceAtLeast(1)

        // --- 그리드 라인 (Y축) ---
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = paddingTop + plotHeight * (1f - i.toFloat() / gridLines)
            drawLine(
                color = surfaceColor,
                start = Offset(paddingLeft, y),
                end = Offset(chartWidth - paddingRight, y),
                strokeWidth = 1f,
            )
            val labelValue = maxDamage * i / gridLines
            val label = formatAxisLabel(labelValue)
            val textResult = textMeasurer.measure(
                text = AnnotatedString(label),
                style = axisTextStyle,
            )
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(
                    x = (paddingLeft - textResult.size.width - 6f).coerceAtLeast(0f),
                    y = y - textResult.size.height / 2f,
                ),
            )
        }

        // --- X축 레이블 ---
        val xLabels = 6
        for (i in 0..xLabels) {
            val second = maxSecond * i / xLabels
            val x = paddingLeft + plotWidth * i.toFloat() / xLabels
            drawLine(
                color = surfaceColor,
                start = Offset(x, paddingTop),
                end = Offset(x, chartHeight - paddingBottom),
                strokeWidth = 1f,
            )
            val label = "${second}s"
            val textResult = textMeasurer.measure(
                text = AnnotatedString(label),
                style = axisTextStyle,
            )
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(
                    x = x - textResult.size.width / 2f,
                    y = chartHeight - paddingBottom + 8f,
                ),
            )
        }

        // --- 라인 그래프 ---
        val points = snapshots.map { snapshot ->
            val x = paddingLeft + plotWidth * snapshot.second.toFloat() / maxSecond
            val y = paddingTop + plotHeight * (1f - (snapshot.cumulativeDamage / maxDamage).toFloat())
            Offset(x.coerceIn(paddingLeft, chartWidth - paddingRight), y.coerceIn(paddingTop, chartHeight - paddingBottom))
        }

        if (points.isNotEmpty()) {
            // 라인
            val linePath = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(
                path = linePath,
                color = primaryColor,
                style = Stroke(width = 2.5f),
            )

            // 채우기 (아래 영역)
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, chartHeight - paddingBottom)
                lineTo(points.first().x, chartHeight - paddingBottom)
                close()
            }
            drawPath(
                path = fillPath,
                color = primaryFill,
            )
        }

        // --- 축 ---
        drawLine(
            color = onSurfaceColor,
            start = Offset(paddingLeft, chartHeight - paddingBottom),
            end = Offset(chartWidth - paddingRight, chartHeight - paddingBottom),
            strokeWidth = 1.5f,
        )
        drawLine(
            color = onSurfaceColor,
            start = Offset(paddingLeft, paddingTop),
            end = Offset(paddingLeft, chartHeight - paddingBottom),
            strokeWidth = 1.5f,
        )
    }
}
