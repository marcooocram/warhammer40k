package control

import model.Model
import model.ModelFromCsv
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exception.ResourceException
import io.blackmo18.kotlin.grass.dsl.grass
import model.CombatUnit
import java.io.File
import model.UnitFromCsv
import model.Weapon
import model.WeaponFromCSV
import java.net.URL


class EagerAllResourceReader(
    weaponsResourceURL: URL? = null,
    targetsResourceURL: URL? = null,
    unitsResourceURL: URL? = null,
    classLoader: ClassLoader = object {}.javaClass.classLoader
) : ResourceReader {

    private val weaponsResource = weaponsResourceURL?.file ?: "weapons.csv"
    private val weaponsFile = File(weaponsResourceURL?.toURI() ?: classLoader.getResource(weaponsResource)?.toURI()
    ?: throw ResourceException("URI to weapon resources missing for path: $weaponsResource"))

    private val targetsResource = targetsResourceURL?.file ?: "targets.csv"
    private val targetsFile = File(targetsResourceURL?.toURI() ?: classLoader.getResource(targetsResource)?.toURI()
    ?: throw ResourceException("URI to target resources missing for path: $targetsResource")
    )

    private val unitsResource = unitsResourceURL?.file ?: "combatunits.csv"
    private val unitsFile = File(unitsResourceURL?.toURI() ?: classLoader.getResource(unitsResource)?.toURI()
    ?: throw ResourceException("URI to unit resources missing for path: $unitsResource"))

    private val weaponsCsv = csvReader().readAllWithHeader(weaponsFile)
    private val targetsCsv = csvReader().readAllWithHeader(targetsFile)
    private val unitsCsv = csvReader().readAllWithHeader(unitsFile)

    @OptIn(ExperimentalStdlibApi::class)
    private val weapons = grass<WeaponFromCSV>().harvest(weaponsCsv).map { it.toWeapon() }

    @OptIn(ExperimentalStdlibApi::class)
    private val targets = grass<ModelFromCsv>().harvest(targetsCsv).map { it.toModel() }

    @OptIn(ExperimentalStdlibApi::class)
    private val units = grass<UnitFromCsv>().harvest(unitsCsv).map { it.toCombatUnit(this) }

    override fun weapons(): List<Weapon> = weapons
    override fun targets(): List<Model> = targets
    override fun units(): List<CombatUnit> = units
}