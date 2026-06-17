package com.mssimulator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val id: String,
    val name: String,
    @SerialName("job") val jobName: String,
    val damage: Double = 0.0,
    @SerialName("hit_count") val hitCount: Int = 1,
    @SerialName("mob_count") val mobCount: Int = 1,
    val cooltime: Long = 0L,
    val delay: Long = 0L,
    val duration: Long = 0L,
    @SerialName("is_buff") val isBuff: Boolean = false,
    @SerialName("is_summon") val isSummon: Boolean = false,
    @SerialName("is_attack") val isAttack: Boolean = true,
)

/** Damage per single use = damage% * hitCount */
fun Skill.damagePerUse(): Double = damage * hitCount

/** DPS ignoring cooldown (raw cast efficiency) */
fun Skill.dps(): Double = if (delay > 0) damagePerUse() / (delay.toDouble() * 0.001) else 0.0

/** Effective DPS considering cooldown */
fun Skill.effectiveDps(): Double {
    if (cooltime <= 0) return dps()
    val cycleTime = (delay + cooltime).toDouble() * 0.001
    return if (cycleTime > 0) damagePerUse() / cycleTime else 0.0
}

@Serializable
data class SkillData(
    val version: String = "",
    val jobs: Map<String, List<Skill>> = emptyMap(),
)
