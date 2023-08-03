package model

import io.kotest.common.runBlocking
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testData.TestUnits

class WeaponKeywordTest {

    @Test
    fun shouldSerializeStringsCorrectly() {
        WeaponKeyword.fromMultiString(";heavy;SUSTaiNEDHITS;;") shouldContainAll  listOf(WeaponKeyword.HEAVY, WeaponKeyword.SUSTAINEDHITS)
    }

    @Test
    fun blastShouldBeCorrect() = runBlocking{
        WeaponKeyword.BLAST.rules.size shouldBe 1
        val sut = WeaponKeyword.BLAST.rules.first()

        val testCases = listOf(
            row(TestUnits.oneManUnit, TestUnits.oneManUnit, DmgMod()),
            row(TestUnits.oneManUnit, TestUnits.fiveManUnit, DmgMod()),
            row(TestUnits.oneManUnit, TestUnits.tenManUnit, DmgMod()),
            row(TestUnits.oneManUnit, TestUnits.twentyManUnit, DmgMod()),

            row(TestUnits.fiveManUnit, TestUnits.oneManUnit, DmgMod(plusShots = 1)),
            row(TestUnits.fiveManUnit, TestUnits.fiveManUnit, DmgMod(plusShots = 1)),
            row(TestUnits.fiveManUnit, TestUnits.tenManUnit, DmgMod(plusShots = 1)),
            row(TestUnits.fiveManUnit, TestUnits.twentyManUnit, DmgMod(plusShots = 1)),

            row(TestUnits.tenManUnit, TestUnits.oneManUnit, DmgMod(plusShots = 2)),
            row(TestUnits.tenManUnit, TestUnits.fiveManUnit, DmgMod(plusShots = 2)),
            row(TestUnits.tenManUnit, TestUnits.tenManUnit, DmgMod(plusShots = 2)),
            row(TestUnits.tenManUnit, TestUnits.twentyManUnit, DmgMod(plusShots = 2)),

            row(TestUnits.twentyManUnit, TestUnits.oneManUnit, DmgMod(plusShots = 4)),
            row(TestUnits.twentyManUnit, TestUnits.fiveManUnit, DmgMod(plusShots = 4)),
            row(TestUnits.twentyManUnit, TestUnits.tenManUnit, DmgMod(plusShots = 4)),
            row(TestUnits.twentyManUnit, TestUnits.twentyManUnit, DmgMod(plusShots = 4))
        )

        forAll(*testCases.toTypedArray()){targetUnit: CombatUnit,shootingUnit:CombatUnit, expected: DmgMod ->
            sut.dmgMod(targetUnit, shootingUnit) shouldBe expected
            sut.condition(targetUnit, shootingUnit) shouldBe true
        }
    }
}