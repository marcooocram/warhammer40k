import control.*
import model.*

@ExcludeFromJacocoGeneratedReport
fun main() {

    val resourceReader: ResourceReader = EagerAllResourceReader()

    val shootingclaculator: ShootinCalculator = SimpleShootingCalculator()
    val weapon = resourceReader.weapons()[0]
    val target = resourceReader.targets()[0]
    val damage = shootingclaculator.estimatedDamage(weapon, target)

    println("weapon: $weapon")
    println("target: $target")

    println("damage: $damage")

    println(resourceReader.weapons().find { it.name.contains("frag", true) })
    println("findAll: " + resourceReader.find("name", "Space"))
    val castellanLauncher = resourceReader.find("name", "Castellan Launcher", Weapon::class.java).first()
    println("findWeapon: $castellanLauncher")

    val knight = resourceReader.find("name", "WAR DOG EXECUTIONER", Model::class.java).first()
    val shootingUnit = resourceReader.find("name","Desolation Squad" , CombatUnit::class.java).first()


    val twoDesolationSquads = shootingUnit.merge(shootingUnit)
    val tenManDesolationSquad = resourceReader.find(mapOf("name" to "Desolation Squad"), CombatUnit::class.java )


    println("doubledeso: $twoDesolationSquads")
    println("10mandeso: $tenManDesolationSquad")

    println(shootingclaculator.estimateLosses(shootingUnit, shootingUnit))

    println("castellan launcher")
    println("knighthit: " + shootingclaculator.estimatedDamage(castellanLauncher, knight))
    println("marine hit: " + shootingclaculator.estimatedDamage(castellanLauncher, target ))
}