package com.mssimulator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mssimulator.data.DataRepository
import com.mssimulator.engine.SimEngine
import com.mssimulator.model.*
import kotlinx.coroutines.withTimeout

enum class AppTab { Input, Result2Min, Result6Min, BossClear }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val selectedTab = remember { mutableStateOf(AppTab.Input) }
    val specState = remember { mutableStateOf(CharacterSpec()) }
    val selectedJob = remember { mutableStateOf("") }
    val jobNames = remember { mutableStateOf(emptyList<String>()) }
    val skillsByJob = remember { mutableStateOf(emptyMap<String, List<Skill>>()) }
    val bossList = remember { mutableStateOf(emptyList<Boss>()) }
    val sim2min = remember { mutableStateOf<SimulationResult?>(null) }
    val sim6min = remember { mutableStateOf<SimulationResult?>(null) }
    val simBosses = remember { mutableStateOf(emptyMap<String, SimulationResult>()) }
    val loading = remember { mutableStateOf(true) }
    val errorMsg = remember { mutableStateOf("") }

    // Load all data at startup — never leave loading=true
    LaunchedEffect(Unit) {
        try {
            val repo = DataRepository()
            try {
                val skillResult = try {
                    withTimeout(10_000) { repo.fetchAllSkills() }
                } catch (e: Exception) {
                    Result.failure<SkillData>(e)
                }
                val bossResult = try {
                    withTimeout(10_000) { repo.fetchBosses() }
                } catch (e: Exception) {
                    Result.failure<List<Boss>>(e)
                }

                if (skillResult.isSuccess && bossResult.isSuccess) {
                    val sd = skillResult.getOrThrow()
                    jobNames.value = sd.jobs.keys.toList().sorted()
                    skillsByJob.value = sd.jobs
                    bossList.value = bossResult.getOrThrow()
                } else {
                    val sample = repo.getSampleSkillData()
                    jobNames.value = sample.jobs.keys.toList().sorted()
                    skillsByJob.value = sample.jobs
                    bossList.value = repo.getSampleBossData().bosses
                    val reason = skillResult.exceptionOrNull()?.message ?: bossResult.exceptionOrNull()?.message ?: "fetch failed"
                    errorMsg.value = "GitHub load failed ($reason), using sample data"
                }
            } finally {
                repo.release()
            }
        } catch (e: Exception) {
            // Worst-case: load sample data directly
            val repo = DataRepository()
            val sample = repo.getSampleSkillData()
            jobNames.value = sample.jobs.keys.toList().sorted()
            skillsByJob.value = sample.jobs
            bossList.value = repo.getSampleBossData().bosses
            errorMsg.value = "Emergency fallback: ${e.message ?: e::class.simpleName}"
        } finally {
            loading.value = false
        }
    }

    val currentSkills = selectedJob.value.let { job -> skillsByJob.value[job] ?: emptyList() }

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
                    selected = selectedTab.value == AppTab.Input,
                    onClick = { selectedTab.value = AppTab.Input },
                    label = { Text("Setup") },
                    icon = {}
                )
                if (sim2min.value != null) {
                    NavigationBarItem(
                        selected = selectedTab.value == AppTab.Result2Min,
                        onClick = { selectedTab.value = AppTab.Result2Min },
                        label = { Text("2min") },
                        icon = {}
                    )
                }
                if (sim6min.value != null) {
                    NavigationBarItem(
                        selected = selectedTab.value == AppTab.Result6Min,
                        onClick = { selectedTab.value = AppTab.Result6Min },
                        label = { Text("6min") },
                        icon = {}
                    )
                }
                if (simBosses.value.isNotEmpty()) {
                    NavigationBarItem(
                        selected = selectedTab.value == AppTab.BossClear,
                        onClick = { selectedTab.value = AppTab.BossClear },
                        label = { Text("Boss") },
                        icon = {}
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when {
                loading.value -> LoadingIndicator()
                else -> when (selectedTab.value) {
                    AppTab.Input -> InputForm(
                        spec = specState.value,
                        onSpecChange = { specState.value = it },
                        selectedJob = selectedJob.value,
                        onJobChange = { selectedJob.value = it },
                        availableJobs = jobNames.value,
                        skills = currentSkills,
                        bosses = bossList.value,
                        loadError = errorMsg.value,
                        onSimulate = {
                            if (currentSkills.isNotEmpty()) {
                                val engine = SimEngine(specState.value)
                                sim2min.value = engine.simulateForDuration(currentSkills, 120)
                                sim6min.value = engine.simulateForDuration(currentSkills, 360)
                                simBosses.value = bossList.value.associate { b ->
                                    b.name to engine.simulateBossClear(currentSkills, b)
                                }
                            }
                        },
                    )

                    AppTab.Result2Min -> sim2min.value?.let {
                        ResultPanel(it, "2min ${it.durationSeconds}s damage")
                    }

                    AppTab.Result6Min -> sim6min.value?.let {
                        ResultPanel(it, "6min ${it.durationSeconds}s damage")
                    }

                    AppTab.BossClear -> BossClearPanel(simBosses.value)
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Loading game data from GitHub...")
        }
    }
}
