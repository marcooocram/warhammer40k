package control

import model.CombatUnit

interface Simulator {
    fun simulateSimpleFight(combatUnit1: CombatUnit, combatUnit2: CombatUnit, numberOfTurns: Int): Pair<CombatUnit, CombatUnit>
    fun sumilateBigFight(army1: List<CombatUnit>, army2: List<CombatUnit>, numberOfTurns: Int)
}