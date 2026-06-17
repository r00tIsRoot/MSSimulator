package com.mssimulator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Boss(
    val id: String,
    val name: String,
    val hp: Long = 0L,
    val level: Int = 0,
    val defense: Int = 300,
    @SerialName("min_level") val minLevel: Int = 0,
)

@Serializable
data class BossData(
    val version: String = "",
    val bosses: List<Boss> = emptyList(),
)

@Serializable
data class SimulationResult(
    val totalDamage: Double = 0.0,
    val durationSeconds: Int = 0,
    val dpm: Double = 0.0,
    val rotation: List<String> = emptyList(),
    val snapshots: List<DamageSnapshot> = emptyList(),
    val bossName: String = "",
    val bossHp: Long = 0L,
    val estimatedClearSeconds: Double = 0.0,
)

@Serializable
data class DamageSnapshot(
    val second: Int = 0,
    val cumulativeDamage: Double = 0.0,
    val damageThisSecond: Double = 0.0,
)
