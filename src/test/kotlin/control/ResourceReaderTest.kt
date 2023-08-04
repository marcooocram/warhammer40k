package control

import exception.ItemNotFoundException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import model.CombatUnit
import model.Model
import model.Weapon
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testData.TestUnits.fiveManUnit
import testData.TestUnits.oneManUnit
import testData.TestUnits.simpleSoldier
import testData.TestUnits.testMarine
import testData.TestUnits.twentyManUnit
import testData.TestUnits.unitToFind
import testData.TestWeapons.searchDecoy
import testData.TestWeapons.simpleWeapon
import kotlin.test.assertFailsWith

class ResourceReaderTest {

    private var sut: ResourceReader = TestableResourceReader()

    @BeforeEach
    fun init() {
        sut = TestableResourceReader()
    }

    @Test
    fun shouldFindTheCorrectOne(){
        val searchResult = sut.find(attributeName = "name", attributeValue = "needle")
        searchResult.size shouldBe  1
        searchResult.first() shouldBe unitToFind
    }

    @Test
    fun shouldFindTheCorrectUnit() {
        (sut as TestableResourceReader).weapons.add(searchDecoy)

        val allTypesSearchResult = sut.find(attributeName = "name", attributeValue = "needle")
        allTypesSearchResult.size shouldBe  2

        val searchResult = sut.find(attributeName = "name", attributeValue = "needle", CombatUnit::class.java )
        searchResult.size shouldBe  1
        searchResult.first() shouldBe unitToFind

        val exception = assertFailsWith<ItemNotFoundException>(
            block = {
                sut.find(attributeName = "nonExistingAttributeName", attributeValue = "needle", CombatUnit::class.java )
            }
        )
        exception.message shouldContain "nonExistingAttributeName"
        exception.message shouldContain "CombatUnit"
    }

    @Test
    fun shouldFindCorrectUnitGivenMultipleAttributes() {
        (sut as TestableResourceReader).weapons.add(searchDecoy)
        val attributes = mapOf("name" to "needle", "additionalName" to "additionalNeedleName")
        val nonExistingAttributes = mapOf("name" to "needle", "nonExistingAttributeName" to "additionalNeedleName")


        val searchResult = sut.find(attributes, CombatUnit::class.java )
        searchResult.size shouldBe  1
        searchResult.first() shouldBe unitToFind


        val exception = assertFailsWith<ItemNotFoundException>(
            block = {
                sut.find(nonExistingAttributes, CombatUnit::class.java )
            }
        )
        exception.message shouldContain "nonExistingAttributeName"
        exception.message shouldContain "CombatUnit"
    }


    @Test
    fun shouldNotFailForUnknownAttributes(){
        val searchResult = sut.find(attributeName = "nonExistingAttributeName", attributeValue = "needle")
        searchResult.size shouldBe  0
    }

}

private class TestableResourceReader: ResourceReader {
    val weapons = mutableListOf(simpleWeapon)
    val targets = mutableListOf(simpleSoldier, testMarine(simpleWeapon))
    val units = mutableListOf(unitToFind, twentyManUnit, fiveManUnit(), oneManUnit)

    override fun weapons(): List<Weapon> {
        return weapons
    }

    override fun targets(): List<Model> {
        return targets
    }

    override fun units(): List<CombatUnit> {
       return units
    }
}