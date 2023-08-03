package model

import control.ResourceReader
import java.math.BigDecimal

data class Model(
    val name: String,
    val savingThrow: Int,
    val inVulSavingThrow: Int,
    val feelNoPain: Int,
    val toughness: Int,
    val wounds: BigDecimal,
    val frequency: String,
){

    fun asCombatUnit() = CombatUnit.fromModelAndWeapon(this)

    companion object {
        fun fromString(input:String, resourceReader: ResourceReader): Map<Model, Int> {
            val modelAndQuantityMap:MutableMap<Model, Int> = HashMap()
            input.split("+", ignoreCase = true).filter { it.isNotEmpty() }.map { quantityAndName ->
                val qan = quantityAndName.trim().split("*")
                modelAndQuantityMap.put(resourceReader.targets().find { it.name.contains(qan[1].trim()) }!!, Integer.parseInt(qan[0]))
            }
            return modelAndQuantityMap
        }

        val DefaultSpaceMarine = Model(
            name = "DefaultSpaceMarine",
            savingThrow = 3,
            inVulSavingThrow = 7,
            feelNoPain = 7,
            toughness = 4,
            wounds = BigDecimal(2),
            frequency = "1.0",
        )
    }
}

data class ModelFromCsv(
    val name: String,
    val savingThrow: Int,
    val inVulSavingThrow: Int,
    val feelNoPain: Int,
    val toughness: Int,
    val wounds: Int,
    val frequency: String,
) {
    fun toModel(): Model {
        return Model(
            name = this.name,
            savingThrow = this.savingThrow,
            inVulSavingThrow = this.inVulSavingThrow,
            feelNoPain = this.feelNoPain,
            toughness = this.toughness,
            wounds = BigDecimal(this.wounds),
            frequency = this.frequency,
        )

    }
}
