package com.mssimulator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mssimulator.model.Boss
import com.mssimulator.model.CharacterSpec
import com.mssimulator.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm(
    spec: CharacterSpec,
    onSpecChange: (CharacterSpec) -> Unit,
    selectedJob: String,
    onJobChange: (String) -> Unit,
    availableJobs: List<String>,
    skills: List<Skill>,
    bosses: List<Boss>,
    loadError: String = "",
    onSimulate: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // --- 데이터 출처 정보 ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MS Simulator", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Data: github.com/r00tIsRoot/MSSimulatorData",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (loadError.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            loadError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${availableJobs.size} jobs, ${bosses.size} bosses loaded",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // --- 직업 선택 ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("직업 선택", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    if (availableJobs.isEmpty()) {
                        Text("Loading...", style = MaterialTheme.typography.bodySmall)
                    } else {
                        JobChips(
                            jobs = availableJobs,
                            selected = selectedJob,
                            onSelect = onJobChange,
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${skills.size} skills loaded",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (skills.isNotEmpty()) {
                        Text(
                            "2분/6분 딜량이 스킬 계수(%) 기반으로 자동 계산되었습니다",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        // --- 스펙 입력 ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Character Spec", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "보스 클리어 타임 계산에만 사용됩니다 (2분/6분 딜량은 스펙 무관)",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(12.dp))

                    SpecTextField("Level", spec.level.toString(), { v ->
                        onSpecChange(spec.copy(level = v.toIntOrNull() ?: spec.level))
                    })
                    SpecTextField("Main Stat", spec.mainStat.toString(), { v ->
                        onSpecChange(spec.copy(mainStat = v.toIntOrNull() ?: spec.mainStat))
                    })
                    SpecTextField("Sub Stat", spec.subStat.toString(), { v ->
                        onSpecChange(spec.copy(subStat = v.toIntOrNull() ?: spec.subStat))
                    })
                    SpecTextField("ATK/MATK", spec.attackPower.toString(), { v ->
                        onSpecChange(spec.copy(attackPower = v.toIntOrNull() ?: spec.attackPower))
                    })
                    SpecTextField("Boss Damage (%)", spec.bossDamage.toString(), { v ->
                        onSpecChange(spec.copy(bossDamage = v.toDoubleOrNull() ?: spec.bossDamage))
                    })
                    SpecTextField("Ignore DEF (%)", spec.ignoreDefense.toString(), { v ->
                        onSpecChange(spec.copy(ignoreDefense = v.toDoubleOrNull() ?: spec.ignoreDefense))
                    })
                    SpecTextField("Crit Rate (%)", spec.critRate.toString(), { v ->
                        onSpecChange(spec.copy(critRate = v.toDoubleOrNull() ?: spec.critRate))
                    })
                    SpecTextField("Crit Damage (%)", spec.critDamage.toString(), { v ->
                        onSpecChange(spec.copy(critDamage = v.toDoubleOrNull() ?: spec.critDamage))
                    })
                    SpecTextField("Final Damage (%)", spec.finalDamage.toString(), { v ->
                        onSpecChange(spec.copy(finalDamage = v.toDoubleOrNull() ?: spec.finalDamage))
                    })
                }
            }
        }

        // --- 시뮬레이션 실행 버튼 ---
        item {
            Button(
                onClick = onSimulate,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = skills.isNotEmpty(),
            ) {
                if (skills.isEmpty()) {
                    Text("직업을 먼저 선택하세요")
                } else {
                    Text("보스 클리어 계산 (스펙 기반)")
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun JobChips(
    jobs: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    @Composable
    fun ChipRow(items: List<String>) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            items.forEach { job ->
                FilterChip(
                    selected = job == selected,
                    onClick = { onSelect(job) },
                    label = { Text(job, style = MaterialTheme.typography.bodySmall) },
                )
            }
        }
    }

    Column {
        jobs.chunked(3).forEach { row -> ChipRow(row) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
    Spacer(Modifier.height(8.dp))
}
