package control

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import model.CombatUnit
import org.junit.jupiter.api.Test
import testData.ComparisonFunctions.deepEquals

import testData.TestUnits.fiveManUnit
import testData.TestUnits.testMarine
import testData.TestUnits.woundedTestMarine
import testData.TestWeapons.heavyWeapon
import testData.TestWeapons.simpleWeapon
import java.math.BigDecimal


class DamageDrivenDeathOrderCalculatorTest {

    @Test
    fun orderModels() {
            val shootingCalculatorMock = mockk<ShootinCalculator>()

            every { shootingCalculatorMock.estimatedDamage(heavyWeapon, any()) } returns BigDecimal("2")
            every { shootingCalculatorMock.estimatedDamage(simpleWeapon, any()) } returns BigDecimal("1")


            with(DamageDrivenDeathOrderCalculator()) {
                fiveManUnit().orderModels(shootingCalculatorMock) shouldBe fiveManUnit()

                val fivePlusOne = fiveManUnit().addModel(testMarine(heavyWeapon))
                val onePlusFive = CombatUnit.fromModel(testMarine(heavyWeapon)).addModel(testMarine(simpleWeapon)).addModel(testMarine(simpleWeapon)).addModel(testMarine(simpleWeapon)).addModel(testMarine(simpleWeapon)).addModel(testMarine(simpleWeapon))
                fivePlusOne shouldNotBe  onePlusFive
                fivePlusOne.orderModels(shootingCalculatorMock).deepEquals( onePlusFive.orderModels(shootingCalculatorMock), listOf("name", "totalPoints"))


                fiveManUnit().addModel(woundedTestMarine).orderModels(shootingCalculatorMock).models.keys.first() shouldBe woundedTestMarine
            }

    }
}