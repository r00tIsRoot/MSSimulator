package com.mssimulator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    onSimulate: () -> Unit,
    onJobsLoaded: (List<String>) -> Unit,
    onSkillsLoaded: (List<Skill>) -> Unit,
    onBossesLoaded: (List<Boss>) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // --- JSON 데이터 로드 섹션 (샘플 내장) ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("샘플 데이터", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "GitHub에서 JSON 데이터를 로드하거나 빌트인 샘플 데이터를 사용합니다.",
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

                    val jobOptions = if (availableJobs.isEmpty())
                        listOf("히어로", "팔라딘", "다크나이트", "아크메이지(불/독)", "아크메이지(썬/콜)", "비숍", "보우마스터", "신궁", "패스파인더", "나이트로드", "섀도어", "듀얼블레이더")
                    else availableJobs

                    AutocompleteChips(
                        options = jobOptions,
                        selected = selectedJob,
                        onSelect = { onJobChange(it) }
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "선택된 스킬: ${skills.size}개",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // --- 스펙 입력 ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("캐릭터 스펙", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    SpecTextField("레벨", spec.level.toString(), { v ->
                        onSpecChange(spec.copy(level = v.toIntOrNull() ?: spec.level))
                    })
                    SpecTextField("주스탯", spec.mainStat.toString(), { v ->
                        onSpecChange(spec.copy(mainStat = v.toIntOrNull() ?: spec.mainStat))
                    })
                    SpecTextField("부스탯", spec.subStat.toString(), { v ->
                        onSpecChange(spec.copy(subStat = v.toIntOrNull() ?: spec.subStat))
                    })
                    SpecTextField("공격력/마력", spec.attackPower.toString(), { v ->
                        onSpecChange(spec.copy(attackPower = v.toIntOrNull() ?: spec.attackPower))
                    })
                    SpecTextField("보스 데미지 (%)", spec.bossDamage.toString(), { v ->
                        onSpecChange(spec.copy(bossDamage = v.toDoubleOrNull() ?: spec.bossDamage))
                    })
                    SpecTextField("방어율 무시 (%)", spec.ignoreDefense.toString(), { v ->
                        onSpecChange(spec.copy(ignoreDefense = v.toDoubleOrNull() ?: spec.ignoreDefense))
                    })
                    SpecTextField("크리티컬 확률 (%)", spec.critRate.toString(), { v ->
                        onSpecChange(spec.copy(critRate = v.toDoubleOrNull() ?: spec.critRate))
                    })
                    SpecTextField("크리티컬 데미지 (%)", spec.critDamage.toString(), { v ->
                        onSpecChange(spec.copy(critDamage = v.toDoubleOrNull() ?: spec.critDamage))
                    })
                    SpecTextField("최종 데미지 (%)", spec.finalDamage.toString(), { v ->
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
                    Text("먼저 직업을 선택해주세요")
                } else {
                    Text("시뮬레이션 실행 (${skills.size}개 스킬)")
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun AutocompleteChips(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column {
        options.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { option ->
                    FilterChip(
                        selected = option == selected,
                        onClick = { onSelect(option) },
                        label = { Text(option, style = MaterialTheme.typography.bodySmall) },
                    )
                }
            }
        }
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
