package model

import java.math.BigDecimal

data class Rule(
    val dmgMod: (target: CombatUnit, shooter: CombatUnit) -> DmgMod,
    val condition: (target: CombatUnit, shooter: CombatUnit) -> Boolean,
    val removed: Boolean = false,
){
    fun remove(): Rule = this.copy(removed = true)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

        if (dmgMod != other.dmgMod) return false
        return condition == other.condition
    }

    override fun hashCode(): Int {
        var result = dmgMod.hashCode()
        result = 31 * result + condition.hashCode()
        return result
    }

    companion object {
        fun always(dmgMod: (target: CombatUnit, shooter: CombatUnit) -> DmgMod) : Rule = Rule(
            dmgMod = dmgMod,
            condition = { _, _ -> true}
        )

        private val basicRulesAndNames: Map<String, Rule>  =
            mapOf(
                Pair("No Advance And shoot",
                    Rule({ _, _ -> DmgMod(dmgMultiplier = BigDecimal.ZERO) } , { _, shooter -> shooter.hasAdvanced})
                ),
            )

        private val basicRules: List<Rule> = basicRulesAndNames.values.toList()

        fun basicRule(name:String): Rule =
            basicRulesAndNames.filter{(key, _) -> name == key }.values.first()

        fun mergeWithBasic(additionalRules: List<Rule>): List<Rule> {
            val rulesToRemove = additionalRules.filter { it.removed }.distinct()
            val rulesToAdd = additionalRules.filterNot { it.removed }.distinct()
            val remainingBasicRules = basicRules.filterNot { rulesToRemove.contains(it) }

            return rulesToAdd.plus(remainingBasicRules).distinct()
        }
    }
}

enum class RerollCondition{
    ALL,
    ALLMISSES,
    ONES,
    NONE
}

data class DmgMod(
    val plusShots: Int = 0,
    val plusToHit: Int = 0,
    val plusAp: Int = 0,
    val plusToWound: Int = 0,
    val rerollHits: RerollCondition = RerollCondition.NONE,
    val rerollWounds: RerollCondition = RerollCondition.NONE,
    val rerollDmg: RerollCondition = RerollCondition.NONE,
    val dmgMultiplier: BigDecimal = BigDecimal(1),
)
