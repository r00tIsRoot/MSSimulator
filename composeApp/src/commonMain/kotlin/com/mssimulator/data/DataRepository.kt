package com.mssimulator.data

import com.mssimulator.model.Boss
import com.mssimulator.model.BossData
import com.mssimulator.model.Skill
import com.mssimulator.model.SkillData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

/**
 * Data repository that fetches game data from GitHub raw JSON.
 *
 * Default: https://raw.githubusercontent.com/r00tIsRoot/MSSimulatorData/main/
 *
 * Files expected at repo root:
 *   - skills.json
 *   - bosses.json
 */
class DataRepository(
    private val baseUrl: String = GITHUB_DATA_URL,
) {
    companion object {
        const val GITHUB_DATA_URL = "https://raw.githubusercontent.com/r00tIsRoot/MSSimulatorData/main"
    }

    private val client = HttpClient()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    suspend fun fetchSkills(jobName: String): Result<List<Skill>> = runCatching {
        val data = fetchAllSkills().getOrThrow()
        data.jobs[jobName] ?: emptyList()
    }

    suspend fun fetchAllSkills(): Result<SkillData> = runCatching {
        val response = client.get("$baseUrl/skills.json").bodyAsText()
        json.decodeFromString<SkillData>(response)
    }

    suspend fun fetchBosses(): Result<List<Boss>> = runCatching {
        val response = client.get("$baseUrl/bosses.json").bodyAsText()
        val data = json.decodeFromString<BossData>(response)
        data.bosses
    }

    /** Parse raw JSON string (for fallback/offline) */
    fun parseSkillData(jsonString: String): SkillData {
        return json.decodeFromString<SkillData>(jsonString)
    }

    fun parseBossData(jsonString: String): BossData {
        return json.decodeFromString<BossData>(jsonString)
    }

    /** Embedded sample data fallback */
    fun getSampleSkillData(): SkillData = parseSkillData(SAMPLE_SKILLS_JSON)
    fun getSampleBossData(): BossData = parseBossData(SAMPLE_BOSSES_JSON)

    fun release() {
        client.close()
    }
}
