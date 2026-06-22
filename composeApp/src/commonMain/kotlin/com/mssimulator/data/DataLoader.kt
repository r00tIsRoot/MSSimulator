package com.mssimulator.data

private const val SKILLS_URL = "https://raw.githubusercontent.com/r00tIsRoot/MSSimulatorData/main/skills.json"
private const val BOSSES_URL = "https://raw.githubusercontent.com/r00tIsRoot/MSSimulatorData/main/bosses.json"

/** Platform-specific HTTP GET that returns response body as text. */
expect suspend fun fetchText(url: String): String

/** Fetches skills.json from MSSimulatorData GitHub repo. */
suspend fun fetchSkillsJson(): String = fetchText(SKILLS_URL)

/** Fetches bosses.json from MSSimulatorData GitHub repo. */
suspend fun fetchBossesJson(): String = fetchText(BOSSES_URL)
