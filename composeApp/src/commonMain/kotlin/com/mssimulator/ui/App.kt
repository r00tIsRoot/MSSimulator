package com.mssimulator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mssimulator.data.SAMPLE_SKILLS_JSON
import com.mssimulator.data.SAMPLE_BOSSES_JSON
import com.mssimulator.engine.SimEngine
import com.mssimulator.font.koreanFontFamily
import com.mssimulator.model.*
import kotlinx.serialization.json.Json

enum class AppTab { Input, Result2Min, Result6Min, BossClear }

private val json = Json { ignoreUnknownKeys = true; isLenient = true }

private data class AppData(
    val jobs: Map<String, List<Skill>>,
    val bosses: List<Boss>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val koreanFf = koreanFontFamily()
    val appTypography = remember(koreanFf) {
        val f = koreanFf
        with(Typography()) {
            copy(
                displayLarge = displayLarge.copy(fontFamily = f),
                displayMedium = displayMedium.copy(fontFamily = f),
                displaySmall = displaySmall.copy(fontFamily = f),
                headlineLarge = headlineLarge.copy(fontFamily = f),
                headlineMedium = headlineMedium.copy(fontFamily = f),
                headlineSmall = headlineSmall.copy(fontFamily = f),
                titleLarge = titleLarge.copy(fontFamily = f),
                titleMedium = titleMedium.copy(fontFamily = f),
                titleSmall = titleSmall.copy(fontFamily = f),
                bodyLarge = bodyLarge.copy(fontFamily = f),
                bodyMedium = bodyMedium.copy(fontFamily = f),
                bodySmall = bodySmall.copy(fontFamily = f),
                labelLarge = labelLarge.copy(fontFamily = f),
                labelMedium = labelMedium.copy(fontFamily = f),
                labelSmall = labelSmall.copy(fontFamily = f),
            )
        }
    }

    val selectedTab = remember { mutableStateOf(AppTab.Input) }
    val specState = remember { mutableStateOf(CharacterSpec()) }
    val selectedJob = remember { mutableStateOf("") }
    val jobNames = remember { mutableStateOf(emptyList<String>()) }
    val skillsByJob = remember { mutableStateOf(emptyMap<String, List<Skill>>()) }
    val bossList = remember { mutableStateOf(emptyList<Boss>()) }
    val sim2min = remember { mutableStateOf<SimulationResult?>(null) }
    val sim6min = remember { mutableStateOf<SimulationResult?>(null) }
    val simBosses = remember { mutableStateOf(emptyMap<String, SimulationResult>()) }
    val statusMsg = remember { mutableStateOf("") }

    // Load embedded data once at startup — no network, instant
    LaunchedEffect(Unit) {
        try {
            val sd = json.decodeFromString<SkillData>(SAMPLE_SKILLS_JSON)
            val bd = json.decodeFromString<BossData>(SAMPLE_BOSSES_JSON)
            jobNames.value = sd.jobs.keys.toList().sorted()
            skillsByJob.value = sd.jobs
            bossList.value = bd.bosses
            statusMsg.value = "${sd.jobs.size} jobs, ${bd.bosses.size} bosses loaded"
        } catch (e: Exception) {
            statusMsg.value = "Data error: ${e.message}"
        }
    }

    val currentSkills = selectedJob.value.let { job -> skillsByJob.value[job] ?: emptyList() }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
            primaryContainer = androidx.compose.ui.graphics.Color(0xFF1A237E),
            secondary = androidx.compose.ui.graphics.Color(0xFFCE93D8),
            surface = androidx.compose.ui.graphics.Color(0xFF1E1E2E),
            background = androidx.compose.ui.graphics.Color(0xFF121220),
            onSurface = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
            onBackground = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
        ),
        typography = appTypography,
    ) {
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
                when (selectedTab.value) {
                    AppTab.Input -> InputForm(
                        spec = specState.value,
                        onSpecChange = { specState.value = it },
                        selectedJob = selectedJob.value,
                        onJobChange = { selectedJob.value = it },
                        availableJobs = jobNames.value,
                        skills = currentSkills,
                        bosses = bossList.value,
                        loadError = statusMsg.value,
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
                    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Run simulation first")
                    }
                    AppTab.Result6Min -> sim6min.value?.let {
                        ResultPanel(it, "6min ${it.durationSeconds}s damage")
                    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Run simulation first")
                    }
                    AppTab.BossClear -> BossClearPanel(simBosses.value)
                }
            }
        }
    }
}
