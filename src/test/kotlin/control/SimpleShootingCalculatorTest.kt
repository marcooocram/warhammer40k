package control

import io.kotest.common.runBlocking
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import model.*
import org.junit.jupiter.api.Test
import testData.TestUnits.simpleSoldier
import testData.TestWeapons.simpleWeapon
import java.math.BigDecimal
import java.math.RoundingMode

class SimpleShootingCalculatorTest {

    private val sut: ShootinCalculator = SimpleShootingCalculator()

    @Test
    fun estimatedDamage() = runBlocking{
        val testCases = listOf(
            row(simpleWeapon, simpleSoldier, BigDecimal("1").divide(BigDecimal("8"), 2, RoundingMode.HALF_UP)),
            row(simpleWeapon, simpleSoldier.copy(savingThrow = 7), BigDecimal("1").divide(BigDecimal("4"), 2, RoundingMode.HALF_UP)),
            row(simpleWeapon, simpleSoldier.copy(feelNoPain = 4), BigDecimal("1").divide(BigDecimal("16"), 2, RoundingMode.HALF_UP)),
        )

        forAll(*testCases.toTypedArray()){weapon:Weapon, target: Model, expected: BigDecimal ->
            sut.estimatedDamage(weapon, target) shouldBeEqualComparingTo expected
        }
    }

    @Test
    fun toHit() = runBlocking{
        val method = sut.javaClass.getDeclaredMethod("getHits", Weapon::class.java, List::class.java)
        method.isAccessible = true

        val testCases = listOf(
            row(simpleWeapon, listOf(DmgMod()), BigDecimal("0.50")),
            row(simpleWeapon, listOf(DmgMod(plusToHit = 1)), BigDecimal("0.67")),
            row(simpleWeapon, listOf(DmgMod(plusToHit = 1), DmgMod(plusToHit = 1)), BigDecimal("0.67")),
            row(simpleWeapon, listOf(DmgMod(plusToHit = -1)), BigDecimal("0.33")),
            row(simpleWeapon, listOf(DmgMod(plusToHit = -8),DmgMod(plusToHit = -1)), BigDecimal("0.33")),
            row(simpleWeapon.copy(toHit= 6), listOf(DmgMod()), BigDecimal("0.17")),
            row(simpleWeapon.copy(toHit= 6), listOf(DmgMod(plusToHit = 1)), BigDecimal("0.33")),
            row(simpleWeapon.copy(toHit= 6), listOf(DmgMod(plusToHit = -1)), BigDecimal("0.17"))
        )

        forAll(*testCases.toTypedArray()){weapon:Weapon, dmgMods: List<DmgMod>, expected: BigDecimal ->
            val parameters = arrayOfNulls<Any>(2)
            parameters[0] = weapon
            parameters[1] = dmgMods

            method.invoke(sut, *parameters) shouldBe expected
        }
    }

    @Test
    fun getWoundsPerHit() = runBlocking{
        val method = sut.javaClass.getDeclaredMethod("getWoundsPerHit", Weapon::class.java, Model::class.java, List::class.java)
        method.isAccessible = true

        val testCases = listOf(
            row(simpleWeapon, simpleSoldier,  listOf(DmgMod()), BigDecimal("0.50")),
            row(simpleWeapon, simpleSoldier, listOf(DmgMod(plusToWound = 1)), BigDecimal("0.67")),
            row(simpleWeapon, simpleSoldier,listOf(DmgMod(plusToWound = 1), DmgMod(plusToWound = 1)), BigDecimal("0.67")),
            row(simpleWeapon, simpleSoldier, listOf(DmgMod(plusToWound = -1)), BigDecimal("0.33")),
            row(simpleWeapon, simpleSoldier, listOf(DmgMod(plusToWound = -8),DmgMod(plusToWound = -1)), BigDecimal("0.33")),
            row(simpleWeapon.copy(strength = 2), simpleSoldier,listOf(DmgMod()), BigDecimal("0.17")),
            row(simpleWeapon.copy(strength = 2), simpleSoldier,listOf(DmgMod(plusToWound = 1)), BigDecimal("0.33")),
            row(simpleWeapon.copy(strength = 2), simpleSoldier,listOf(DmgMod(plusToWound = -1)), BigDecimal("0.17")),
            row(simpleWeapon.copy(strength = 3), simpleSoldier,listOf(DmgMod()), BigDecimal("0.33")),
            row(simpleWeapon.copy(strength = 5), simpleSoldier,listOf(DmgMod()), BigDecimal("0.67")),
            row(simpleWeapon.copy(strength = 7), simpleSoldier,listOf(DmgMod()), BigDecimal("0.67")),
            row(simpleWeapon.copy(strength = 8), simpleSoldier,listOf(DmgMod()), BigDecimal("0.83")),
            row(simpleWeapon.copy(strength = 9), simpleSoldier,listOf(DmgMod(plusToWound = 1)), BigDecimal("0.83")),
            row(simpleWeapon.copy(strength = 9), simpleSoldier,listOf(DmgMod(plusToWound = -1)), BigDecimal("0.67")),
        )

        forAll(*testCases.toTypedArray()){weapon:Weapon, target: Model, dmgMods: List<DmgMod>, expected: BigDecimal ->
            val parameters = arrayOfNulls<Any>(3)
            parameters[0] = weapon
            parameters[1] = target
            parameters[2] = dmgMods

            method.invoke(sut, *parameters) shouldBe expected
        }
    }

    @Test
    fun estimateLosses() {
    }
}