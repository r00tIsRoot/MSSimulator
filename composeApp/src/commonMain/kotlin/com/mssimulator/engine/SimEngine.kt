package com.mssimulator.engine

import com.mssimulator.model.*

class SimEngine(private val spec: CharacterSpec? = null) {

    /**
     * 계수(%) 기반 시뮬레이션 — 캐릭터 스펙과 무관하게 스킬 계수(damage% × hitCount)만으로
     * 딜량을 계산한다. 직업만 선택하면 즉시 계산 가능하다. (보스 클리어 제외)
     */
    fun simulateCoefficient(
        skills: List<Skill>,
        durationSeconds: Int,
    ): SimulationResult = runSimulation(skills, durationSeconds, boss = null) { skill ->
        skill.damagePerUse()
    }

    /**
     * 스펙 기반 시뮬레이션 — 캐릭터 스펙으로 절대 딜량을 계산한다. 보스 클리어 타임 산정에 사용.
     */
    fun simulateForDuration(
        skills: List<Skill>,
        durationSeconds: Int,
        boss: Boss? = null,
    ): SimulationResult {
        val activeSpec = spec ?: CharacterSpec()
        return runSimulation(skills, durationSeconds, boss) { skill ->
            activeSpec.calculateSkillDamage(skill, boss?.defense ?: 300)
        }
    }

    private fun runSimulation(
        skills: List<Skill>,
        durationSeconds: Int,
        boss: Boss?,
        damageOf: (Skill) -> Double,
    ): SimulationResult {
        if (skills.isEmpty()) return SimulationResult()

        // 1. 각 스킬의 1회 데미지 계산
        val skillDamages = skills.associateWith { skill -> damageOf(skill) }

        val totalTimeMs = durationSeconds * 1000L
        val snapshots = mutableListOf<DamageSnapshot>()
        var currentTimeMs = 0L
        var totalDamage = 0.0
        var lastSnapshotSecond = -1

        // 쿨타임 관리: skill -> next available time (ms)
        val cooldownMap = mutableMapOf<Skill, Long>().withDefault { 0L }

        // 버프 관리: buff skill -> remaining time
        val activeBuffs = mutableMapOf<Skill, Long>()

        // 소환수 관리
        val activeSummons = mutableMapOf<Skill, Long>()

        // 사용된 스킬 로그
        val rotationLog = mutableListOf<String>()

        // 버프 스킬과 어택 스킬 분리
        val buffSkills = skills.filter { it.isBuff && it.duration > 0 }
        val attackSkills = skills.filter { it.isAttack }
        val summonSkills = skills.filter { it.isSummon }

        // 버프는 시작하자마자 사용 (pre-casting)
        for (buff in buffSkills) {
            cooldownMap[buff] = currentTimeMs // immediate use
        }

        // 어택 스킬을 효과적인 DPS 순으로 정렬 (skip if delay=0 or damage=0)
        val sortedAttackSkills = attackSkills
            .filter { it.delay > 0 && it.damage > 0 }
            .sortedByDescending { it.effectiveDps() }

        while (currentTimeMs < totalTimeMs) {
            // --- 초 단위 스냅샷 기록 ---
            val currentSecond = (currentTimeMs / 1000).toInt()
            if (currentSecond > lastSnapshotSecond) {
                snapshots.add(
                    DamageSnapshot(
                        second = currentSecond,
                        cumulativeDamage = totalDamage,
                        damageThisSecond = 0.0,
                    )
                )
                lastSnapshotSecond = currentSecond
            }

            // --- 버프 유지 및 갱신 ---
            for (buff in buffSkills) {
                if (buff.duration <= 0) continue
                if (currentTimeMs >= cooldownMap.getValue(buff) && currentTimeMs < totalTimeMs) {
                    // 버프 사용
                    cooldownMap[buff] = currentTimeMs + buff.duration + buff.delay
                    activeBuffs[buff] = currentTimeMs + buff.duration
                    rotationLog.add("${buff.name}[BUFF]")
                    if (buff.delay > 0) {
                        // 버프 시전 시간 소비
                        currentTimeMs += buff.delay
                        continue
                    }
                }

                // 버프 만료 확인
                if (activeBuffs.containsKey(buff) && currentTimeMs >= activeBuffs.getValue(buff)) {
                    activeBuffs.remove(buff)
                }
            }

            // --- 소환수 유지 ---
            for (summon in summonSkills) {
                if (summon.duration <= 0) continue
                val hasSummon = activeSummons.containsKey(summon) &&
                        currentTimeMs < activeSummons.getValue(summon)
                if (!hasSummon && currentTimeMs >= cooldownMap.getValue(summon) && currentTimeMs < totalTimeMs) {
                    // 소환
                    cooldownMap[summon] = currentTimeMs + summon.cooltime
                    activeSummons[summon] = currentTimeMs + summon.duration
                    rotationLog.add("${summon.name}[SUMMON]")
                    if (summon.delay > 0) {
                        currentTimeMs += summon.delay
                        continue
                    }
                }
                // 소환수 데미지 (소환 중일 때 매 초)
                if (hasSummon) {
                    // 매 초 소환수가 공격했다고 가정
                    val summonDmg = skillDamages[summon] ?: 0.0
                    totalDamage += summonDmg * (summon.hitCount.coerceAtLeast(1))
                }
            }

            // --- 어택 스킬 선택 및 사용 ---
            var skillUsed = false
            for (skill in sortedAttackSkills) {
                val nextAvailable = cooldownMap.getValue(skill)
                if (currentTimeMs >= nextAvailable) {
                    // 이 스킬 사용!
                    cooldownMap[skill] = currentTimeMs + skill.delay + skill.cooltime
                    val dmg = skillDamages[skill] ?: 0.0
                    totalDamage += dmg
                    rotationLog.add(skill.name)
                    currentTimeMs += skill.delay
                    skillUsed = true
                    break
                }
            }

            if (!skillUsed) {
                // 사용 가능한 스킬이 없음 → 가장 빨리 쿨이 돌아오는 스킬까지 대기
                val nextReady = cooldownMap.values.minOrNull() ?: (currentTimeMs + 10)
                currentTimeMs = nextReady.coerceAtMost(totalTimeMs)
            }
        }

        // 마지막 스냅샷 보정
        if (snapshots.isNotEmpty()) {
            val finalSnapshot = DamageSnapshot(
                second = durationSeconds,
                cumulativeDamage = totalDamage,
                damageThisSecond = 0.0,
            )
            snapshots.add(finalSnapshot)
        }

        // 초당 데미지 계산
        for (i in (snapshots.size - 1) downTo 1) {
            val delta = snapshots[i].cumulativeDamage - snapshots[i - 1].cumulativeDamage
            snapshots[i] = snapshots[i].copy(
                damageThisSecond = delta.coerceAtLeast(0.0)
            )
        }

        return SimulationResult(
            totalDamage = totalDamage,
            durationSeconds = durationSeconds,
            dpm = if (durationSeconds > 0) totalDamage / durationSeconds.toDouble() * 60.0 else 0.0,
            rotation = rotationLog,
            snapshots = snapshots,
            bossName = boss?.name ?: "",
            bossHp = boss?.hp ?: 0L,
            estimatedClearSeconds = if (totalDamage > 0)
                (boss?.hp?.toDouble() ?: 0.0) / totalDamage * durationSeconds
            else 0.0,
        )
    }

    fun simulateBossClear(
        skills: List<Skill>,
        boss: Boss,
    ): SimulationResult {
        val durationSeconds = 360 // 6분 기준
        val result = simulateForDuration(skills, durationSeconds, boss)
        val dps = if (durationSeconds > 0) result.totalDamage / durationSeconds else 0.0
        val clearSeconds = if (dps > 0) boss.hp / dps else 0.0

        return result.copy(
            estimatedClearSeconds = clearSeconds,
        )
    }
}
