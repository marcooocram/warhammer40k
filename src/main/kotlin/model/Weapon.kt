package model

import control.ResourceReader
import java.math.BigDecimal

data class WeaponFromCSV(
    val name: String,
    val keywords: String? ,
    val strength: Int,
    val armourPiercing: Int,
    val damage: String,
    val shots: String,
    val toHit: Int,
) {
    fun toWeapon(): Weapon {
        return Weapon(
            name = this.name,
            keywords = this.keywords?.let { WeaponKeyword.fromMultiString(it) } ?: emptyList(),
            strength = this.strength,
            armourPiercing = this.armourPiercing,
            damage = this.damage,
            shots = Value.averageFromString(this.shots),
            toHit = this.toHit,
        )
    }
}

data class Weapon(
    val name: String,
    val keywords: List<WeaponKeyword>,
    val strength: Int,
    val armourPiercing: Int,
    val damage: String,
    val shots: BigDecimal,
    val toHit: Int,
) {
    companion object{
        fun fromString(input:String, resourceReader: ResourceReader): Map<Weapon, Int> {
            val weaponAndQuantityMap:MutableMap<Weapon, Int> = HashMap()
            input.split("+", ignoreCase = true).filter { it.isNotEmpty() }.map { quantityAndName ->
                val qan = quantityAndName.trim().split("*")
                weaponAndQuantityMap.put(resourceReader.weapons().find { it.name.contains(qan[1].trim()) }!!, Integer.parseInt(qan[0]))
            }
            return weaponAndQuantityMap
        }
    }
}