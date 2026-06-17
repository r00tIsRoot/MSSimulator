package com.mssimulator.data

import com.mssimulator.model.Boss
import com.mssimulator.model.BossData
import com.mssimulator.model.Skill
import com.mssimulator.model.SkillData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class DataRepository(
    private val baseUrl: String = "https://raw.githubusercontent.com/your-org/ms-data/main"
) {
    private val client = HttpClient()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    suspend fun fetchSkills(jobName: String): Result<List<Skill>> = runCatching {
        val response = client.get("$baseUrl/skills.json").bodyAsText()
        val data = json.decodeFromString<SkillData>(response)
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

    fun parseSkillData(jsonString: String): SkillData {
        return json.decodeFromString<SkillData>(jsonString)
    }

    fun parseBossData(jsonString: String): BossData {
        return json.decodeFromString<BossData>(jsonString)
    }

    fun release() {
        client.close()
    }
}
