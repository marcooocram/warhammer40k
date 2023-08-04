package testData

import model.CombatUnit
import model.Model
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
    )


    val testMarine = Model(
        name =  "testMarine",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a"
    )

    val testMarineWithDifferentName = Model(
        name =  "testMarineWithDifferentName",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a"
    )

    val oneManUnit  = CombatUnit(
        name = "tenTEstMarines",
        additionalName ="",
        weapons = emptyMap(),
        models = mapOf(testMarine to 1),
        totalPoints = 0
    )

    val fiveManUnit  = CombatUnit(
        name = "tenTEstMarines",
        additionalName ="",
        weapons = emptyMap(),
        models = mapOf(testMarine to 5),
        totalPoints = 0
    )

    val tenManUnit  = CombatUnit(
        name = "tenTEstMarines",
        additionalName ="",
        weapons = emptyMap(),
        models = mapOf(testMarine to 10),
        totalPoints = 0
    )

    val twentyManUnit  = CombatUnit(
        name = "tenTEstMarines",
        additionalName ="",
        weapons = emptyMap(),
        models = mapOf(testMarine to 20),
        totalPoints = 0
    )

    val unitToFind  = CombatUnit(
        name = "needle",
        additionalName ="additionalNeedleName",
        weapons = emptyMap(),
        models = mapOf(testMarine to 20),
        totalPoints = 0
    )

    val unitWithAsteriskInName  = CombatUnit(
        name = "1*2*3*",
        additionalName ="additionalNeedleName",
        weapons = emptyMap(),
        models = mapOf(testMarine to 20),
        totalPoints = 0
    )
}