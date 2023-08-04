package control

import io.kotest.common.runBlocking
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import model.*
import org.junit.jupiter.api.Test
import testData.TestUnits.fiveManUnit
import testData.TestUnits.simpleSoldier
import testData.TestUnits.testMarine
import testData.TestUnits.testMarineWithDifferentName
import testData.TestWeapons.heavyWeapon
import testData.TestWeapons.simpleWeapon
import testData.TestWeapons.superHeavyWeapon
import testData.TestWeapons.weaponWithKeyWord
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
    fun estimateLosses() = runBlocking {
        val fiveManUnitWithGuns = fiveManUnit.copy(weapons = mapOf(weaponWithKeyWord to 5))
        val fiveManUnitWithGuns2 = fiveManUnit.copy(weapons = mapOf(simpleWeapon to 25))
        val fiveManUnitWithHeavyGun = fiveManUnit.copy(weapons = mapOf(heavyWeapon to 1))
        val fiveManUnitWithSuperHeavyGun = fiveManUnit.copy(weapons = mapOf(superHeavyWeapon to 1))
        val fiveManUnitWithHeavyGuns = fiveManUnit.copy(weapons = mapOf(heavyWeapon to 5))
        val oneManUnit = fiveManUnit.copy(models = mapOf(testMarine to 1))

        val testCases = listOf(
            row(fiveManUnitWithGuns, fiveManUnit, BigDecimal("0.40")),
            row(fiveManUnitWithGuns2, fiveManUnit, BigDecimal("2.00")),
            row(fiveManUnitWithHeavyGun, fiveManUnit, BigDecimal("0.83")),
            row(fiveManUnitWithHeavyGuns, fiveManUnit, BigDecimal("4.15")),
            row(fiveManUnitWithHeavyGun, oneManUnit, BigDecimal("8.83")),
            row(fiveManUnitWithSuperHeavyGun, fiveManUnit, BigDecimal("8.30")),
        )
        forAll(*testCases.toTypedArray()){shootingUnit: CombatUnit, targetUnit: CombatUnit, expectedDamage: BigDecimal ->
            val fallen  = expectedDamage.toInt() / 2
            val fourManUnit: CombatUnit? = if (fallen >= 4) { null } else fiveManUnit.copy(models = mapOf(testMarine to 4 - fallen))
            val injuredSoldier = fiveManUnit.copy(models = mapOf(testMarine.copy(wounds = testMarine.wounds - (expectedDamage - BigDecimal(fallen).multiply(BigDecimal("2.00")))) to 1))
            sut.estimateLosses(shootingUnit, targetUnit).models shouldBe injuredSoldier.merge(fourManUnit).models
        }
    }

    @Test
    fun estimateLossesForWipe() = runBlocking {
        val fiveManUnitWithHeavyGuns = fiveManUnit.copy(weapons = mapOf(heavyWeapon to 50))
        val fiveManUnitWithGuns2 = fiveManUnit.copy(weapons = mapOf(simpleWeapon to 25))
        val oneManUnit = fiveManUnit.copy(models = mapOf(testMarine to 1))
        val diverseUnit = fiveManUnit.copy(models = mapOf(testMarineWithDifferentName to 1  , testMarine to 1))

        val testCases = listOf(
            row(fiveManUnitWithHeavyGuns, oneManUnit),
            row(fiveManUnitWithHeavyGuns, diverseUnit),
            row(fiveManUnitWithGuns2, oneManUnit),
        )
        forAll(*testCases.toTypedArray()){shootingUnit: CombatUnit, targetUnit: CombatUnit ->
            sut.estimateLosses(shootingUnit, targetUnit).models.size shouldBe 0
        }
    }

}