package control

import model.CombatUnit

fun interface DeathOrderCalculator {
    fun CombatUnit.orderModels(shootingCalculator: ShootinCalculator) : CombatUnit
}