package control

import exception.ResourceException
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import model.CombatUnit
import model.Model
import model.Weapon
import model.WeaponKeyword
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.net.URL
import kotlin.test.assertFailsWith

class EagerAllResourceReaderTest {

    private val weaponsResourceURL = javaClass.getResource("weapons.csv")
    private val targetsResourceURL = javaClass.getResource("targets.csv")
    private val unitsResourceURL = javaClass.getResource("combatunits.csv")

    private val sut = EagerAllResourceReader(weaponsResourceURL, targetsResourceURL, unitsResourceURL)

    @Test
    fun defaultconstructorShouldFindSomething() {
        val emptyResourceReader = EagerAllResourceReader()
        emptyResourceReader.targets() shouldHaveAtLeastSize(1)
        emptyResourceReader.units() shouldHaveAtLeastSize(1)
        emptyResourceReader.weapons() shouldHaveAtLeastSize(1)
    }

    @Test
    fun checkNullSafetyInResourceReader(){
        val urlMock = mockk<URL>()
        every { urlMock.toURI() } returns null
        every { urlMock.file } returns null
        val emptyResourceReader = EagerAllResourceReader(urlMock, urlMock, urlMock)
        emptyResourceReader.targets() shouldHaveAtLeastSize(1)
        emptyResourceReader.units() shouldHaveAtLeastSize(1)
        emptyResourceReader.weapons() shouldHaveAtLeastSize(1)
    }

    @Test
    fun checkExceptionHandling(){
        val saneUrlMock = mockk<URL>()
        every { saneUrlMock.toURI() } returns null
        every { saneUrlMock.file } returns null

        val inSaneUrlMock = mockk<URL>()
        every { inSaneUrlMock.toURI() } returns null
        every { inSaneUrlMock.file } returns "somethingNonExistent"

        val weaponException = assertFailsWith<ResourceException>(
            block = { EagerAllResourceReader(inSaneUrlMock, saneUrlMock, saneUrlMock) }
        )
        val targetException = assertFailsWith<ResourceException>(
            block = { EagerAllResourceReader(saneUrlMock, inSaneUrlMock, saneUrlMock) }
        )
        val unitException = assertFailsWith<ResourceException>(
            block = { EagerAllResourceReader(saneUrlMock, saneUrlMock, inSaneUrlMock) }
        )
        weaponException.message shouldContain "weapon"
        targetException.message shouldContain "target"
        unitException.message shouldContain "unit"
    }

    @Test
    fun handleClassLoaderExceptionsGracefully(){

        val maliciousResourceMock = mockk<URL>()
        every { maliciousResourceMock.toURI() } returns null
        every { maliciousResourceMock.file } returns "maliciousResource"

        val fineResourceMock = mockk<URL>()
        every { fineResourceMock.toURI() } returns null
        every { fineResourceMock.file } returns "fineResource"

        val fineResourceMockWithURI = mockk<URL>()
        every { fineResourceMock.toURI() } returns object {}.javaClass.classLoader.getResource("weapons.csv")!!.toURI()
        every { fineResourceMock.file } returns "fineResource"

        val classLoaderMock = mockk<ClassLoader>()
        every {classLoaderMock.getResource("maliciousResource")} returns null
        every {classLoaderMock.getResource("fineResource")} returns fineResourceMockWithURI

        val weaponException = assertFailsWith<ResourceException>(
            block = { EagerAllResourceReader(maliciousResourceMock, fineResourceMock, fineResourceMock, classLoaderMock) }
        )
        val targetException = assertFailsWith<ResourceException>(
            block = { EagerAllResourceReader(fineResourceMock, maliciousResourceMock, fineResourceMock, classLoaderMock) }
        )
        val unitException = assertFailsWith<ResourceException>(
            block = { EagerAllResourceReader(fineResourceMock, fineResourceMock, maliciousResourceMock, classLoaderMock) }
        )

        weaponException.message shouldContain "weapon"
        targetException.message shouldContain "target"
        unitException.message shouldContain "unit"
    }

    @Test
    fun weapons() {
        sut.weapons().size shouldBe 9
        // Grenade Launcher - Frag, Blast, D3, 3, 4, 0, 1
        sut.weapons()[0] shouldBeEqualToComparingFields Weapon(
            name = "Grenade Launcher - Frag",
            keywords = listOf(WeaponKeyword.BLAST),
            damage = "1",
            shots = BigDecimal("2.00"),
            strength = 4,
            armourPiercing = 0,
            toHit = 3,
            )
        // Grenade Launcher - Krak, , 1, 3, 9, 2, D3
        sut.weapons()[1] shouldBeEqualToComparingFields Weapon(
            name = "Grenade Launcher - Krak",
            keywords = listOf(),
            damage = "D3",
            shots = BigDecimal("1"),
            strength = 9,
            armourPiercing = 2,
            toHit = 3,
        )
        // Bolt Rifle, Assault;Heavy, 2, 3, 4, 1, 1
        sut.weapons()[2] shouldBeEqualToComparingFields Weapon(
            name = "Bolt Rifle",
            keywords = listOf(WeaponKeyword.ASSAULT, WeaponKeyword.HEAVY),
            damage = "1",
            shots = BigDecimal("2"),
            strength = 4,
            armourPiercing = 1,
            toHit = 3,
        )
        // Master Crafted Bolt Rifle, , 2, 2, 4, 1, 2
        sut.weapons()[3] shouldBeEqualToComparingFields Weapon(
            name = "Master Crafted Bolt Rifle",
            keywords = listOf(),
            damage = "2",
            shots = BigDecimal("2"),
            strength = 4,
            armourPiercing = 1,
            toHit = 2,
        )
        // Heavy Bolt Pistol 2+, Pistol, 1, 2, 4, 1, 1
        sut.weapons()[4] shouldBeEqualToComparingFields Weapon(
            name = "Heavy Bolt Pistol 2+",
            keywords = listOf(WeaponKeyword.PISTOL),
            damage = "1",
            shots = BigDecimal("1"),
            strength = 4,
            armourPiercing = 1,
            toHit = 2,
        )
        // Castellan Launcher, BLAST;INDIRECTFIRE, D3, 3, 4, 0, 1
        sut.weapons()[5] shouldBeEqualToComparingFields Weapon(
            name = "Castellan Launcher",
            keywords = listOf(WeaponKeyword.BLAST, WeaponKeyword.INDIRECTFIRE),
            damage = "1",
            shots = BigDecimal("2.00"),
            strength = 4,
            armourPiercing = 0,
            toHit = 3,
        )
        // Vengor Launcher, BLAST;INDIRECTFIRE, D6, 2, 7, 1, 2
        sut.weapons()[6] shouldBeEqualToComparingFields Weapon(
            name = "Vengor Launcher",
            keywords = listOf(WeaponKeyword.BLAST, WeaponKeyword.INDIRECTFIRE),
            damage = "2",
            shots = BigDecimal("3.50"),
            strength = 7,
            armourPiercing = 1,
            toHit = 2,
        )
        //Superkrak Rocket Launcher, HEAVY, 1, 4, 10, 2, D6 + 1
        sut.weapons()[7] shouldBeEqualToComparingFields Weapon(
            name = "Superkrak Rocket Launcher",
            keywords = listOf(WeaponKeyword.HEAVY),
            damage = "D6 + 1",
            shots = BigDecimal("1"),
            strength = 10,
            armourPiercing = 2,
            toHit = 4,
        )
        //Superfrag Rocket Launcher, HEAVY;BLAST,  D6 + 1, 4, 5, 0, 1
        sut.weapons()[8] shouldBeEqualToComparingFields Weapon(
            name = "Superfrag Rocket Launcher",
            keywords = listOf(WeaponKeyword.HEAVY, WeaponKeyword.BLAST),
            damage = "1",
            shots = BigDecimal("4.50"),
            strength = 5,
            armourPiercing = 0,
            toHit = 4,
        )
    }

    @Test
    fun targets() {
        sut.targets().size shouldBe 2
        //Space Marine, 3, 7, 7, 4, 2, 0.8
        sut.targets()[0] shouldBeEqualToComparingFields Model(
            name = "Space Marine",
            savingThrow = 3,
            inVulSavingThrow = 7,
            feelNoPain = 7,
            toughness = 4,
            wounds = BigDecimal(2),
            frequency = "0.8",
        )
        //Primaris Captain, 3, 4, 7, 4, 5, 0.1
        sut.targets()[1] shouldBeEqualToComparingFields Model(
            name = "Primaris Captain",
            savingThrow = 3,
            inVulSavingThrow = 4,
            feelNoPain = 7,
            toughness = 4,
            wounds = BigDecimal(5),
            frequency = "0.1",
        )
    }

    @Test
    fun units() {
        sut.units().size shouldBe 3
        //Intercessor Squad, GrenadeLauncher_Five_Frag, 1*Grenade Launcher - Frag + 4*Bolt Rifle, 5*Space Marine, 95
        sut.units()[0] shouldBeEqualToComparingFields CombatUnit(
            name = "Intercessor Squad",
            additionalName = "GrenadeLauncher_Five_Frag",
            weapons = mapOf(Pair(sut.weapons()[0], 1), Pair(sut.weapons()[2], 4)),
            models = mapOf(Pair(sut.targets()[0], 5)),
            totalPoints = 95
        )
        //Intercessor Squad, GrenadeLauncher_Five_Krak, 1*Grenade Launcher - Krak + 4*Bolt Rifle, 5*Space Marine, 95
        sut.units()[1] shouldBeEqualToComparingFields CombatUnit(
            name = "Intercessor Squad",
            additionalName = "GrenadeLauncher_Five_Krak",
            weapons = mapOf(Pair(sut.weapons()[1], 1), Pair(sut.weapons()[2], 4)),
            models = mapOf(Pair(sut.targets()[0], 5)),
            totalPoints = 95
        )
        //Desolation Squad, Five_Krak, 1*Vengor Launcher + 5*Castellan Launcher + 4*Superkrak Rocket Launcher ,5*Space Marine, 170
        sut.units()[2] shouldBeEqualToComparingFields CombatUnit(
            name = "Desolation Squad",
            additionalName = "Five_Krak",
            weapons = mapOf(Pair(sut.weapons()[6], 1), Pair(sut.weapons()[5], 5), Pair(sut.weapons()[7], 4)),
            models = mapOf(Pair(sut.targets()[0], 5)),
            totalPoints = 170
        )
    }
}