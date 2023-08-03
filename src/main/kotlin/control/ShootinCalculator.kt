package control

import model.Model
import model.Weapon
import model.CombatUnit
import model.DmgMod
import java.math.BigDecimal

interface ShootinCalculator {

    fun estimatedDamage(weapon: Weapon, target: Model, appliedRules: List<DmgMod> = emptyList()) : BigDecimal

    fun estimateLosses(shootingCombatUnit:CombatUnit, targetCombatUnit: CombatUnit) : CombatUnit

}