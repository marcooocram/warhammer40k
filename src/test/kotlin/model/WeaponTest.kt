package model

import control.ResourceReader
import exception.ItemNotFoundException
import io.kotest.common.runBlocking
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import testData.TestWeapons.simpleWeapon
import java.math.BigDecimal
import kotlin.test.assertFailsWith

class WeaponTest {

    private val resourceReaderMock = mockk<ResourceReader>()
    private val weapons = listOf(
        simpleWeapon
    )

    @Test
    fun csvToWeapon() = runBlocking {
        val testCases = listOf(
            row(
                WeaponFromCSV(
                    name= "test",
                    keywords = null,
                    strength = 3,
                    armourPiercing = 3,
                    damage = "4",
                    shots = "3",
                    toHit = 3,
                ),
                Weapon(
                    name= "test",
                    keywords = emptyList(),
                    strength = 3,
                    armourPiercing = 3,
                    damage = "4",
                    shots = BigDecimal("3"),
                    toHit = 3,
                )
            ),

        )

        forAll(*testCases.toTypedArray()){rawWeapon: WeaponFromCSV, expected: Weapon ->
           rawWeapon.toWeapon() shouldBe expected
        }


    }

    @Test
    fun weaponFromString()= runBlocking {
        every{ resourceReaderMock.weapons()} returns weapons

        val testCases = listOf(
            row(simpleWeapon.name, mapOf(simpleWeapon to 1)),
            row("3*${simpleWeapon.name}", mapOf(simpleWeapon to 3)),
        )

        forAll(*testCases.toTypedArray()){input: String, expected: Map<Weapon, Int> ->
            Weapon.fromString(input, resourceReaderMock) shouldBe expected
        }
    }

    @Test
    fun exception() {
        every{ resourceReaderMock.weapons()} returns weapons

        assertFailsWith<ItemNotFoundException>(
            block = { Weapon.fromString("NonExistingWeapon", resourceReaderMock) }
        )
    }
}