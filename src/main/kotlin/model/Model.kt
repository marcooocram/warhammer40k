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
    val weapons: Map<Weapon, Int>,
    val wounded: Boolean = false
){
    companion object {
        fun fromString(input:String, resourceReader: ResourceReader): Map<Model, Int> {
            val modelAndQuantityMap:MutableMap<Model, Int> = HashMap()
            input.split("+", ignoreCase = true).filter { it.isNotEmpty() }.map { quantityAndName ->
                val qan = quantityAndName.trim().split("*")
                modelAndQuantityMap.put(resourceReader.targets().find { it.name.contains(qan[1].trim()) }!!, Integer.parseInt(qan[0]))
            }
            return modelAndQuantityMap
        }
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
    val weapons: String?,
) {
    fun toModel(resourceReader: ResourceReader): Model {
        return Model(
            name = this.name,
            savingThrow = this.savingThrow,
            inVulSavingThrow = this.inVulSavingThrow,
            feelNoPain = this.feelNoPain,
            toughness = this.toughness,
            wounds = BigDecimal(this.wounds),
            frequency = this.frequency,
            weapons = Weapon.fromString(this.weapons, resourceReader),
        )

    }
}
