package com.mssimulator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mssimulator.chart.DamageLineChart
import com.mssimulator.data.formatLargeNumber
import com.mssimulator.data.formatTime
import com.mssimulator.model.SimulationResult

@Composable
fun ResultPanel(
    result: SimulationResult,
    title: String,
) {
    if (result.totalDamage <= 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("시뮬레이션 결과가 없습니다.")
        }
        return
    }

    val displayDpm = result.dpm
    val displayDamage = result.totalDamage
    val formattedDpm = formatLargeNumber(displayDpm)
    val formattedDamage = formatLargeNumber(displayDamage)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // --- 요약 카드 ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        StatItem("총 딜량", formattedDamage)
                        StatItem("DPM", formattedDpm)
                        StatItem("시간", "${result.durationSeconds}초")
                    }

                    if (result.estimatedClearSeconds > 0) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "예상 클리어 시간: ${formatTime(result.estimatedClearSeconds)}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }

        // --- 딜량 그래프 ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("시간별 누적 딜량", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    DamageLineChart(
                        snapshots = result.snapshots,
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                    )
                }
            }
        }

        // --- 스킬 로그 ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("스킬 사용 로그", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = result.rotation.joinToString(" → ").take(500),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BossClearPanel(results: Map<String, SimulationResult>) {
    if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("보스 데이터가 없습니다.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                "보스 클리어 타임",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        val sorted = results.entries
            .filter { it.value.totalDamage > 0 }
            .sortedBy { it.value.estimatedClearSeconds }

        items(sorted) { (bossName, result) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(bossName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "HP: ${formatLargeNumber(result.bossHp.toDouble())}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            formatTime(result.estimatedClearSeconds),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (result.estimatedClearSeconds <= 600)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error,
                        )
                        Text(
                            "DPM: ${formatLargeNumber(result.dpm)}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
