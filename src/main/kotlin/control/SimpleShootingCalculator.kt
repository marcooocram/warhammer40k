package control

import model.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

class SimpleShootingCalculator :ShootinCalculator {
    override fun estimatedDamage(weapon: Weapon, target: Model, appliedRules: List<DmgMod>): BigDecimal {
        return getHits(weapon, appliedRules)
            .multiply(getWoundsPerHit(weapon, target, appliedRules))
            .multiply(getFailedSavesPerWound(weapon, target))
            .multiply(getDamage(weapon, target))
            .multiply(getFeelNoPainFails(target))
            .setScale(2, RoundingMode.HALF_UP)
    }

    override fun estimateLosses(shootingCombatUnit: CombatUnit, targetCombatUnit: CombatUnit): CombatUnit {
        if (shootingCombatUnit.weapons.isEmpty()) return targetCombatUnit
        val weapons: MutableMap<Weapon, Int> = shootingCombatUnit.weapons.toMutableMap()
        val weaponEntry = weapons.entries.first()

        val targets: MutableMap<Model, Int> = targetCombatUnit.models.toMutableMap()
        val targetEntry = targets.entries.first()

        val appliedRules =
            Rule.mergeWithBasic(weaponEntry.key.keywords.flatMap { it.rules })
                .filter { it.condition(targetCombatUnit, shootingCombatUnit) }
                .map { it.dmgMod(targetCombatUnit, shootingCombatUnit) }

        val inflictedDamage = BigDecimal(weaponEntry.value).multiply(estimatedDamage(weaponEntry.key, targetEntry.key, appliedRules))

        weapons.remove(weaponEntry.key)
        targets.remove(targetEntry.key)

        targets.putAll(getNewTargetMap(mutableMapOf(), inflictedDamage, targetEntry.key, targetEntry.value, targets))

        //TODO also remove weapons if damaged
        return estimateLosses(shootingCombatUnit.copy(weapons = weapons ), targetCombatUnit.copy(models = targets))
    }

    private fun getNewTargetMap(acc:MutableMap<Model, Int>, inflictedDamage: BigDecimal, target: Model, amountTargets: Int, targets: MutableMap<Model, Int>): Map<Model, Int> {
        when {
            inflictedDamage < target.wounds -> {
                acc[target.copy(wounds = target.wounds - inflictedDamage)] = 1
                if (amountTargets > 1) acc[target] = amountTargets - 1
                return acc
            }
            inflictedDamage - target.wounds == BigDecimal("0.00") -> {
                if (amountTargets > 1) acc[target] = amountTargets - 1
                return acc
            }
            else -> {
                if (amountTargets > 1) getNewTargetMap(acc,inflictedDamage - target.wounds, target, amountTargets - 1, targets)
                else if (targets.isNotEmpty()) {
                    val targetEntry = targets.entries.first()
                    targets.remove(targetEntry.key)
                    getNewTargetMap(acc, inflictedDamage - target.wounds, targetEntry.key, targetEntry.value, targets)
                }else {
                    return acc
                }
            }
        }
        return acc
    }

    private fun getHits(weapon: Weapon, rules: List<DmgMod>): BigDecimal {
        val extraShots = BigDecimal(rules.fold(0){acc: Int, rule: DmgMod -> acc + rule.plusShots})

        val hitModifier = getCappedModifier(rules.map { it.plusToHit })

        val toHit = min(6, max(2, weapon.toHit - hitModifier ))

        return (weapon.shots + extraShots).multiply(Roll.ofValue(toHit).successProb())
    }

    private fun getWoundsPerHit(weapon: Weapon, target: Model, rules: List<DmgMod>): BigDecimal {
        val woundModifier = getCappedModifier(rules.map { it.plusToWound })
        val regularRoll =  when {
            weapon.strength >= 2 * target.toughness -> 2
            weapon.strength > target.toughness -> 3
            weapon.strength == target.toughness -> 4
            2 * weapon.strength <= target.toughness -> 6
            else -> 5
        }
        val toWound = min(6, max(2, regularRoll - woundModifier ))
        return Roll.ofValue(toWound).successProb()
    }

    private fun getCappedModifier(values: List<Int>): Int {
        // hit and wound modifiers are capped at + or - 1
        val totalModifiers = values.fold(0){acc: Int, value: Int -> acc + value}
        return when {
            totalModifiers < 0 -> -1
            totalModifiers == 0 -> 0
            else -> 1
        }
    }

    private fun getFailedSavesPerWound(weapon: Weapon, target: Model): BigDecimal {
        val bestSave = (target.savingThrow + weapon.armourPiercing).coerceAtMost(target.inVulSavingThrow)
        return Roll.ofValue(bestSave).failureProb()
    }

    private fun getDamage(weapon: Weapon, target: Model): BigDecimal {
        return Value.effectiveDamage(weapon.damage, target.wounds)
    }

    private fun getFeelNoPainFails( target: Model):BigDecimal {
        return Roll.ofValue(target.feelNoPain).failureProb()
    }
}