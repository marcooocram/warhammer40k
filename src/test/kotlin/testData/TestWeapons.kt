package testData

import model.Weapon
import java.math.BigDecimal

object TestWeapons {
    val simpleWeapon = Weapon(
        name = " s4 BS 4 dmg1",
        keywords = listOf(),
        damage = "1",
        shots = BigDecimal("1"),
        strength = 4,
        armourPiercing = 0,
        toHit = 4,
    )

    val heavyWeapon = Weapon(
        name = " s8 BS 4 dmg6",
        keywords = listOf(),
        damage = "6",
        shots = BigDecimal("1"),
        strength = 8,
        armourPiercing = 6,
        toHit = 4,
    )

    val searchDecoy = Weapon(
        name = "needle",
        keywords = listOf(),
        damage = "1",
        shots = BigDecimal("1"),
        strength = 4,
        armourPiercing = 0,
        toHit = 4,
    )
}