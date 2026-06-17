package com.mssimulator.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterSpec(
    val level: Int = 280,
    val job: String = "",
    val mainStat: Int = 50000,         // 주스탯 (STR/DEX/INT/LUK)
    val subStat: Int = 20000,          // 부스탯
    val attackPower: Int = 2000,       // 공격력 / 마력
    val bossDamage: Double = 400.0,    // 보스 데미지 (%)
    val ignoreDefense: Double = 95.0,  // 방어율 무시 (%)
    val finalDamage: Double = 100.0,   // 최종 데미지 (%)
    val critRate: Double = 100.0,      // 크리티컬 확률 (%)
    val critDamage: Double = 150.0,    // 크리티컬 데미지 (%)
    val damageMultiplier: Double = 100.0, // 데미지 (%)
    val armorBreak: Double = 0.0,      // 방어구 관통 (일부 직업)
)

/**
 * 단일 스킬의 최종 데미지 계산 (보스 기준)
 * damageMultiplier = 공격력 * (주스탯 * 4 + 부스탯) * 0.01 * (1 + 보공/100) * (1 + 최종뎀/100)
 * 실제 데미지 = damageMultiplier * (스킬 퍼뎀/100) * hitCount
 */
fun CharacterSpec.calculateSkillDamage(skill: Skill, defense: Int = 300): Double {
    // 주스탯 계수: (주스탯 * 4 + 부스탯) * 0.01
    val statFactor = (mainStat * 4.0 + subStat) * 0.01

    // 공격력 * 주스탯 계수
    val rawMultiplier = attackPower * statFactor

    // 보스 데미지
    val bossDmgFactor = 1.0 + bossDamage / 100.0

    // 최종 데미지
    val finalDmgFactor = 1.0 + finalDamage / 100.0

    // 방어율 무시 적용: 몬스터 방어율이 defense%일 때
    val effectiveDefense = defense * (1.0 - ignoreDefense / 100.0)
    val defenseFactor = 1.0 - effectiveDefense / 100.0

    // 크리티컬 (확률 = critRate%, 적용 시 critDamage% 추가)
    val critFactor = 1.0 + (critRate / 100.0) * (critDamage / 100.0)

    // 기본 데미지%
    val dmgFactor = 1.0 + damageMultiplier / 100.0

    // 최종 스킬 데미지 배율
    val totalMultiplier = rawMultiplier * bossDmgFactor * finalDmgFactor * defenseFactor * critFactor * dmgFactor

    // 스킬 1회 사용 데미지
    return totalMultiplier * skill.damagePerUse() / 100.0
}
