package control

import model.CombatUnit
import model.Model
import model.Weapon
import java.math.BigDecimal

class DamageDrivenDeathOrderCalculator : DeathOrderCalculator {

    private val defaultMarine = Model(
        name =  "defaultMarine",
        savingThrow = 3,
        inVulSavingThrow = 7,
        feelNoPain = 7,
        toughness = 4,
        wounds = BigDecimal("2.00"),
        frequency = "n/a",
        weapons = emptyMap()
    )

    private val defaultTank = Model(
        name =  "defaultTank",
        savingThrow = 3,
        inVulSavingThrow = 4,
        feelNoPain = 4,
        toughness = 11,
        wounds = BigDecimal("12"),
        frequency = "n/a",
        weapons = emptyMap()
    )

    override fun CombatUnit.orderModels(shootingCalculator: ShootinCalculator): CombatUnit {
        return this.copy(models = this.models.toSortedMap(compareBy<Model> ({ !it.wounded }, { it.getValue(shootingCalculator)})))
    }

    private fun Weapon.getMarineKillingCoefficient(shootingCalculator: ShootinCalculator): BigDecimal {
        return shootingCalculator.estimatedDamage(this, defaultMarine)
    }

    private fun Weapon.getTankKillingCoefficient(shootingCalculator: ShootinCalculator): BigDecimal {
        return shootingCalculator.estimatedDamage(this, defaultTank)
    }

    private fun Weapon.getValue(shootingCalculator: ShootinCalculator): BigDecimal {
        return this.getMarineKillingCoefficient(shootingCalculator).multiply(this.getTankKillingCoefficient(shootingCalculator))
    }

    private fun Model.getValue(shootingCalculator: ShootinCalculator) : BigDecimal {
        return this.weapons.entries.sumOf { it.key.getValue(shootingCalculator).multiply(BigDecimal(it.value)) }
    }
}