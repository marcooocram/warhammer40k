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
        fun fromString(input:String?, resourceReader: ResourceReader): Map<Weapon, Int> {
            if (input == null) return emptyMap()
            val weaponAndQuantityMap:MutableMap<Weapon, Int> = HashMap()
            input.split("+", ignoreCase = true).filter { it.isNotEmpty() }.map { quantityAndName ->
                val split = quantityAndName.trim().split("*")
                val quantity = if (split.size > 1) Integer.parseInt(split[0]) else 1
                val name = if (split.size > 1) split[1].trim() else quantityAndName.trim()
                weaponAndQuantityMap.put(resourceReader.weapons().find { it.name.contains(name) }!!, quantity)
            }
            return weaponAndQuantityMap
        }
    }
}