package control

import exception.ItemNotFoundException
import model.Model
import model.Weapon
import model.CombatUnit

interface ResourceReader {
    fun weapons(): List<Weapon>
    fun targets(): List<Model>
    fun units(): List<CombatUnit>
    fun find(attributeName:String, attributeValue: String):  List<Any> {
        val correctClass: List<Class<*>> = findDistinctTypes().filter { isAttributeInClass(it, attributeName ) }
        val targetLists =  allStuff.filter { list ->
            val listTypes = list.map { it::class.java }
            listTypes.any { correctClass.contains(it) }
        }
       return targetLists.flatMap {
            list -> list.filter { item ->
                val clazz = item::class.java
                val field = clazz.getDeclaredField(attributeName)
                field.isAccessible = true
                field[item].toString().contains(attributeValue)
            }
        }
    }

    fun <T> find(attributeName: String, attributeValue: String, clazz: Class<T> ): List<T> {
        try {
            return allStuff.flatten().filterIsInstance(clazz).filter { item ->
                val itemClass = item!!::class.java
                val field = itemClass.getDeclaredField(attributeName)
                field.isAccessible = true
                field[item].toString().contains(attributeValue)
            }
        } catch (e: Exception){
            throw  ItemNotFoundException("couldnt find: ${clazz.simpleName} with $attributeName = $attributeValue", e)
        }
    }

    fun <T> find(attributes: Map<String, String>, clazz: Class<T> ): List<T> {
        return allStuff.flatten()
            .filterIsInstance(clazz)
            .filter { item ->
                attributes.all { (name, value) ->
                    try {
                        val itemClass = item!!::class.java
                        val field = itemClass.getDeclaredField(name)
                        field.isAccessible = true
                        field[item].toString().contains(value)
                    } catch (e: Exception){
                        throw  ItemNotFoundException("couldnt find: ${clazz.simpleName} with $name = $value", e)
                    }
                }
            }
    }

    private val allStuff: List<List<Any>>
        get() = listOf(this.weapons(), this.targets(), this.units())

    private fun findDistinctTypes(): List<Class<*>> {
        val distinctTypes = mutableListOf<Class<*>>()

        for (list in allStuff) {
            for (item in list) {
                val itemType = item::class.java
                if (!distinctTypes.contains(itemType)) {
                    distinctTypes.add(itemType)
                }
            }
        }
        return distinctTypes
    }

    private fun isAttributeInClass (clazz: Class<*>, attributeName: String):Boolean{
        return try {
            clazz.getDeclaredField(attributeName)
            true
        }catch (e: Exception){
            false
        }
    }
}
