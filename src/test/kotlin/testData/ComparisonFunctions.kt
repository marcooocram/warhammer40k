package testData

import io.kotest.matchers.shouldBe
import model.Model
import kotlin.reflect.KProperty1

object ComparisonFunctions {

    fun Map<Model, Int>.compareUnitsIgnoreWeapons(other: Map<Model,Int>){
        this.map { (k,v) -> k.copy(weapons = emptyMap()) to v } shouldBe other.map { (k,v) -> k.copy(weapons = emptyMap()) to v }
    }


    /*
    * experimental cool test functions that don't work atm
    * */

    fun <T, V, S> Map<T, S>.assertMapEqualsIgnoringProperty(
        other: Map<T, S>,
        propertyToIgnore: String,
        comparator: (V?, V?) -> Boolean = {a, b -> a == b }
    ) {
        if (this.size != other.size) {
            throw AssertionError("Maps have different sizes")
        }

        val property = this.keys.firstOrNull()?.let { key ->
            key::class.members.firstOrNull { it.name == propertyToIgnore } as? KProperty1<T, V>
        }

        for ((keyA, valueA) in this) {
            val valueB = other[keyA] ?: throw AssertionError("Maps are not equal")

            val propertyValueA = property?.get(keyA)
            val propertyValueB = property?.get(keyA)

            if (!comparator(propertyValueA, propertyValueB) || valueA != valueB) {
                throw AssertionError("Maps are not equal")
            }
        }
    }

    fun <T> List<T>.assertListEqualsIgnoringProperty(other: List<T>, propertyToIgnore: String, comparator: (T, T) -> Boolean) {
        if (this.size != other.size) {
            throw AssertionError("Lists have different sizes")
        }

        for (i in this.indices) {
            val elementA = this[i]
            val elementB = other[i]

            val propertyValueA = elementA!!::class.members.find { it.name == propertyToIgnore }?.call(elementA)
            val propertyValueB = elementB!!::class.members.find { it.name == propertyToIgnore }?.call(elementB)

            if (!comparator(elementA, elementB) || propertyValueA != propertyValueB) {
                throw AssertionError("Lists are not equal")
            }
        }
    }
}