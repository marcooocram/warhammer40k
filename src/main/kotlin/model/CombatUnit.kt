package model

import control.ResourceReader
import exception.CombatUnitMergeException

data class  CombatUnit(
    val name: String,
    val additionalName:String,
    val weapons: Map<Weapon, Int>,
    val models: Map<Model, Int>,
    val totalPoints: Int,
    val hasMoved: Boolean = false,
    val hasAdvanced:Boolean = false,
) {
    fun merge(otherUnit: CombatUnit?): CombatUnit {
        if (otherUnit == null) return this
        val thisNameQuantity = splitName(this.name)
        val otherNameQuantity = splitName(otherUnit.name)

        if (thisNameQuantity.first != otherNameQuantity.first) throw CombatUnitMergeException("Merging not possible units have different name")

        val newName = "${thisNameQuantity.second + otherNameQuantity.second}*${thisNameQuantity.first}"

        val newWeapons = this.weapons.toMutableMap().apply {
            otherUnit.weapons.forEach { (k, v) -> merge(k, v) { thisVal, otherVal -> thisVal + otherVal } }
        }

        val newModels = this.models.toMutableMap().apply {
            otherUnit.models.forEach { (k, v) -> merge(k, v) { thisVal, otherVal -> thisVal + otherVal } }
        }

        return CombatUnit(
            name = newName,
            additionalName = this.additionalName,
            weapons = newWeapons,
            models = newModels,
            totalPoints = this.totalPoints + otherUnit.totalPoints
        )
    }

    private fun splitName(name: String): Pair<String, Int> {
        val split = name.trim().split("*")
        return when {
            split.size <= 1 -> Pair(split[0], 1)
            split.size == 2 -> Pair(split[1], split[0].toInt())
            else -> throw CombatUnitMergeException("too many * found in unit name")
        }
    }

    companion object{
        fun fromModelAndWeapon(model: Model? = null,  weapon: Weapon? = null) = CombatUnit(
            name = "single ${model?.name ?: "soldier"}",
            additionalName = "",
            weapons = weapon?.let { mapOf(it to 1) } ?: emptyMap(),
            models = mapOf(Pair( model ?: Model.DefaultSpaceMarine,1)),
            totalPoints = 20
        )
    }
}

data class UnitFromCsv(
    val name: String,
    val additionalName:String,
    val weapons: String,
    val models: String,
    val totalPoints: Int
) {
    fun toCombatUnit(resourceReader: ResourceReader): CombatUnit {
        return CombatUnit(
            name = this.name,
            additionalName = this.additionalName,
            weapons = Weapon.fromString(this.weapons, resourceReader),
            models = Model.fromString(this.models, resourceReader),
            totalPoints = this.totalPoints
        )
    }
}

