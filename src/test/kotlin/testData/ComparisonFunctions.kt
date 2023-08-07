package testData

import io.kotest.matchers.shouldBe
import model.Model
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

object ComparisonFunctions {

    fun <T : Any> T.deepEquals(other: T, ignoreFields: List<String>) {
        if (this === other) return
        if (this.javaClass != other.javaClass) throw AssertionError("items are different classes")

        val thisProperties = this::class.declaredMemberProperties
        val otherProperties = other::class.declaredMemberProperties

        for (property in thisProperties) {
            if (ignoreFields.contains(property.name)) continue

            property.isAccessible = true
            val thisValue = property.call(this)
            val otherValue = otherProperties
                .firstOrNull { it.name == property.name }
                ?.apply { isAccessible = true }
                ?.call(other)

            if (thisValue != otherValue) {
                throw AssertionError("Elements differ in property: ${property.name}")
            }
        }
    }

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