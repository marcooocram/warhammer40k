package model

import exception.CombatUnitMergeException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testData.ComparisonFunctions.compareUnitsIgnoreWeapons
import testData.TestUnits.fiveManUnit
import testData.TestUnits.simpleSoldier
import testData.TestUnits.testMarine
import testData.TestUnits.unitToFind
import testData.TestUnits.unitWithAsteriskInName
import testData.TestWeapons.simpleWeapon
import kotlin.test.assertFailsWith

class CombatUnitTest {

    @Test
    fun merge() {
        val mergeResult = fiveManUnit().merge(fiveManUnit())
        mergeResult.models.compareUnitsIgnoreWeapons(mapOf(testMarine() to 10))
        mergeResult.name shouldBe "2*${fiveManUnit().name}"

        val tripleMerge = mergeResult.merge(fiveManUnit())
        tripleMerge.models.compareUnitsIgnoreWeapons(mapOf(testMarine() to 15))
        tripleMerge.name shouldBe "3*${fiveManUnit().name}"

        val mergedMergeResult = mergeResult.merge(mergeResult)
        mergedMergeResult.models.compareUnitsIgnoreWeapons(mapOf(testMarine() to 20))
        mergedMergeResult.name shouldBe "4*${fiveManUnit().name}"

        val nullmergeResult = fiveManUnit().merge(null)
        nullmergeResult.models.compareUnitsIgnoreWeapons(mapOf(testMarine() to 5))
        nullmergeResult.name shouldBe fiveManUnit().name

        assertFailsWith<CombatUnitMergeException>(
            block = { fiveManUnit().merge(unitToFind) }
        )

        assertFailsWith<CombatUnitMergeException>(
            block = { unitWithAsteriskInName.merge(unitWithAsteriskInName) }
        )
    }

    @Test
    fun fromModelAndWeapon(){
        CombatUnit.fromModel(model = testMarine(simpleWeapon)) shouldBe  CombatUnit(
            name = "single ${testMarine(simpleWeapon).name}",
            additionalName = "",
            models = mapOf(Pair(testMarine(simpleWeapon),1)),
            totalPoints = 20
        )

    }

    @Test
    fun addModel(){
        fiveManUnit(simpleWeapon).addModel(testMarine(simpleWeapon)) shouldBe CombatUnit(
            name = "testMarines",
            additionalName = "",
            models = mapOf(Pair(testMarine(simpleWeapon),6)),
            totalPoints = 0
        )

        fiveManUnit(simpleWeapon).addModel(simpleSoldier) shouldBe CombatUnit(
            name = "testMarines",
            additionalName = "",
            models = mapOf(testMarine(simpleWeapon) to 5, simpleSoldier to 1),
            totalPoints = 0
        )
    }

}