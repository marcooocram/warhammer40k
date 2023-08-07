package testData

import model.CombatUnit
import model.Model
import model.Weapon
import testData.TestWeapons.simpleWeapon
import java.math.BigDecimal

object TestUnits {

    val simpleSoldier = Model(
        name = "T4 W1 4+",
        savingThrow = 4,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("1.00"),
        frequency = "n/a",
        weapons = emptyMap()
    )


    val woundedTestMarine = Model(
        name =  "testMarine",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a",
        weapons = mapOf(simpleWeapon to 1),
        wounded = true
    )

    fun testMarine(weapons: List<Weapon> = emptyList()) = Model(
        name =  "testMarine",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a",
        weapons = weapons.associateWith { 1 }
    )

    fun testMarine(weapon: Weapon? = simpleWeapon, amount: Int = 1 ) = Model(
        name =  "testMarine",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a",
        weapons = mapOf((weapon?: simpleWeapon) to amount)
    )

    val testMarineWithDifferentName = Model(
        name =  "testMarineWithDifferentName",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a",
        weapons = emptyMap()
    )

    val oneManUnit  = CombatUnit(
        name = "testMarines",
        additionalName ="",
        models = mapOf(testMarine() to 1),
        totalPoints = 0
    )

    fun fiveManUnit(weapon: Weapon? = null) = CombatUnit(
        name = "testMarines",
        additionalName ="",
        models = mapOf(testMarine(weapon) to 5),
        totalPoints = 0
    )

    fun fiveManUnit(weapon: Weapon, amount: Int) = CombatUnit(
        name = "testMarines",
        additionalName ="",
        models = mapOf(testMarine(weapon, amount) to 5),
        totalPoints = 0
    )

    val tenManUnit  = CombatUnit(
        name = "testMarines",
        additionalName ="",
        models = mapOf(testMarine() to 10),
        totalPoints = 0
    )

    val twentyManUnit  = CombatUnit(
        name = "testMarines",
        additionalName ="",
        models = mapOf(testMarine() to 20),
        totalPoints = 0
    )

    val unitToFind  = CombatUnit(
        name = "needle",
        additionalName ="additionalNeedleName",
        models = mapOf(testMarine() to 20),
        totalPoints = 0
    )

    val unitWithAsteriskInName  = CombatUnit(
        name = "1*2*3*",
        additionalName ="additionalNeedleName",
        models = mapOf(testMarine() to 20),
        totalPoints = 0
    )
}