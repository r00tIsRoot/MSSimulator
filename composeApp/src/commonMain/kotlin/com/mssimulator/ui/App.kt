package com.mssimulator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mssimulator.engine.SimEngine
import com.mssimulator.model.*

enum class AppTab { Input, Result2Min, Result6Min, BossClear }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(AppTab.Input) }
    var characterSpec by remember {
        mutableStateOf(CharacterSpec())
    }
    var selectedJob by remember { mutableStateOf("") }
    var availableJobs by remember { mutableStateOf(listOf<String>()) }
    var skills by remember { mutableStateOf<List<Skill>>(emptyList()) }
    var bosses by remember { mutableStateOf<List<Boss>>(emptyList()) }
    var result2min by remember { mutableStateOf<SimulationResult?>(null) }
    var result6min by remember { mutableStateOf<SimulationResult?>(null) }
    var bossResults by remember { mutableStateOf<Map<String, SimulationResult>>(emptyMap()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MS Simulator") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == AppTab.Input,
                    onClick = { selectedTab = AppTab.Input },
                    label = { Text("설정") },
                    icon = {}
                )
                if (result2min != null) {
                    NavigationBarItem(
                        selected = selectedTab == AppTab.Result2Min,
                        onClick = { selectedTab = AppTab.Result2Min },
                        label = { Text("2분") },
                        icon = {}
                    )
                }
                if (result6min != null) {
                    NavigationBarItem(
                        selected = selectedTab == AppTab.Result6Min,
                        onClick = { selectedTab = AppTab.Result6Min },
                        label = { Text("6분") },
                        icon = {}
                    )
                }
                if (bossResults.isNotEmpty()) {
                    NavigationBarItem(
                        selected = selectedTab == AppTab.BossClear,
                        onClick = { selectedTab = AppTab.BossClear },
                        label = { Text("보스") },
                        icon = {}
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when (selectedTab) {
                AppTab.Input -> InputForm(
                    spec = characterSpec,
                    onSpecChange = { characterSpec = it },
                    selectedJob = selectedJob,
                    onJobChange = { selectedJob = it },
                    availableJobs = availableJobs,
                    skills = skills,
                    bosses = bosses,
                    onSimulate = {
                        val engine = SimEngine(characterSpec)
                        if (skills.isNotEmpty()) {
                            result2min = engine.simulateForDuration(skills, 120)
                            result6min = engine.simulateForDuration(skills, 360)
                            bossResults = bosses.associate { boss ->
                                boss.name to engine.simulateBossClear(skills, boss)
                            }
                        }
                    },
                    onJobsLoaded = { availableJobs = it },
                    onSkillsLoaded = { skills = it },
                    onBossesLoaded = { bosses = it },
                )

                AppTab.Result2Min -> result2min?.let {
                    ResultPanel(it, "2분 ${it.durationSeconds}초 딜량")
                }

                AppTab.Result6Min -> result6min?.let {
                    ResultPanel(it, "6분 ${it.durationSeconds}초 딜량")
                }

                AppTab.BossClear -> BossClearPanel(bossResults)
            }
        }
    }
}
