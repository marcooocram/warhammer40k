package model

import exception.CombatUnitMergeException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testData.TestUnits.fiveManUnit
import testData.TestUnits.testMarine
import testData.TestUnits.unitToFind
import testData.TestUnits.unitWithAsteriskInName
import testData.TestWeapons.simpleWeapon
import kotlin.test.assertFailsWith

class CombatUnitTest {

    @Test
    fun merge() {
        val mergeResult = fiveManUnit.merge(fiveManUnit)
        mergeResult.models shouldBe mapOf(testMarine to 10)
        mergeResult.name shouldBe "2*${fiveManUnit.name}"

        val tripleMerge = mergeResult.merge(fiveManUnit)
        tripleMerge.models shouldBe mapOf(testMarine to 15)
        tripleMerge.name shouldBe "3*${fiveManUnit.name}"

        val mergedMergeResult = mergeResult.merge(mergeResult)
        mergedMergeResult.models shouldBe mapOf(testMarine to 20)
        mergedMergeResult.name shouldBe "4*${fiveManUnit.name}"

        val nullmergeResult = fiveManUnit.merge(null)
        nullmergeResult.models shouldBe mapOf(testMarine to 5)
        nullmergeResult.name shouldBe fiveManUnit.name

        assertFailsWith<CombatUnitMergeException>(
            block = { fiveManUnit.merge(unitToFind) }
        )

        assertFailsWith<CombatUnitMergeException>(
            block = { unitWithAsteriskInName.merge(unitWithAsteriskInName) }
        )
    }

    @Test
    fun fromModelAndWeapon(){
        CombatUnit.fromModelAndWeapon() shouldBe  CombatUnit(
            name = "single soldier",
            additionalName = "",
            weapons = emptyMap(),
            models = mapOf(Pair(Model.DefaultSpaceMarine,1)),
            totalPoints = 20
        )

        CombatUnit.fromModelAndWeapon(model = testMarine, weapon = simpleWeapon) shouldBe  CombatUnit(
            name = "single ${testMarine.name}",
            additionalName = "",
            weapons = mapOf(simpleWeapon to 1),
            models = mapOf(Pair(testMarine,1)),
            totalPoints = 20
        )

    }
}