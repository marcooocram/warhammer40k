package model

import kotlin.random.Random
import java.math.BigDecimal
import java.math.RoundingMode


enum class Dice (val sides: Int) {
    D6(6),
    D3(3);

    fun roll (): Int {
        return Random.nextInt(1, sides +1)
    }

    val average: BigDecimal = (BigDecimal(sides) + BigDecimal(1.0)).divide(BigDecimal(2), 2, RoundingMode.HALF_UP)
}

enum class Roll(val min: Int, val dice: Dice = Dice.D6) {
    auto(1),
    twoPlus(2),
    threePlus(3),
    fourPlus(4),
    fivePlus(5),
    sixPlus(6),
    impossible(7),
    twoPlusD3(2, Dice.D3),
    threePlusD3(3, Dice.D3),
    impossibleD3(4, Dice.D3);

    fun successProb(reroll: Boolean = false): BigDecimal {
        val simpleRoll: BigDecimal = BigDecimal(dice.sides - min + 1).divide(BigDecimal(dice.sides), 10, RoundingMode.HALF_UP)
        return (if(reroll) BigDecimal(1).minus( (BigDecimal(1).minus(simpleRoll)).pow(2)) else simpleRoll).setScale(2,  RoundingMode.HALF_UP)
    }

    fun failureProb(reroll:Boolean = false): BigDecimal {
        return BigDecimal(1).minus(successProb(reroll))
    }

    companion object {
        fun ofValue(value: Int, dice: Dice = Dice.D6): Roll {
            if (dice === Dice.D3) return when(value){
                1 -> auto
                2 -> twoPlusD3
                3 -> threePlusD3
                else -> {
                    impossibleD3
                }
            }
            return when (value) {
                1 -> auto
                2 -> twoPlus
                3 -> threePlus
                4 -> fourPlus
                5 -> fivePlus 
                6 -> sixPlus
                else -> {
                    impossible
                }
            }
        }
    }
}

object Value {
    fun averageFromString(input:String): BigDecimal {
        return input.split("+", ignoreCase = true)
            .filter { it.isNotEmpty() }
            .map { it.trim() }
            .map { value ->
                if (value.isNumeric()) BigDecimal(value)
                else Dice.valueOf(value).average
            }
            .reduce { acc, value -> acc + value }
    }

    fun effectiveDamage(damageString: String, wounds: BigDecimal): BigDecimal {
        val numerics = mutableListOf(0)
        val dice = mutableListOf<Dice>()
        damageString.split("+", ignoreCase = true)
            .filter { it.isNotEmpty() }
            .map { it.trim() }
            .forEach {
                if (it.isNumeric()) numerics.add(Integer.parseInt(it))
                else dice.add(Dice.valueOf(it))
            }
        val flatDamage = numerics.reduce { acc, value -> acc + value }
        if (BigDecimal(flatDamage + dice.size) >=  wounds) return wounds
        if (dice.isEmpty()) return BigDecimal(flatDamage)
        if (BigDecimal(dice.sumOf{ it.sides } + flatDamage) < wounds) return dice.sumOf { it.average } + BigDecimal(flatDamage)

        val totalOutcomes = BigDecimal(dice.fold(1 ) { acc, singleDice ->
            acc * singleDice.sides
        })

        var totalProb: BigDecimal = BigDecimal.ZERO
        var damage: BigDecimal = BigDecimal.ZERO
        for (i in wounds.toInt() downTo 1 + flatDamage) {
            val favourableOutcomes = BigDecimal(countFavorableOutcomes(0, dice.toMutableList(), i - flatDamage))
            val prob = favourableOutcomes.divide(totalOutcomes, 10, RoundingMode.HALF_UP).minus(totalProb)
            totalProb += prob
            damage += prob.multiply(BigDecimal(i))
        }
        return damage.setScale(2, RoundingMode.HALF_UP)
    }

}

private fun String.isNumeric():Boolean{
    return this.matches("-?\\d+(\\.\\d+)?".toRegex())
}

private fun countFavorableOutcomes(total: Int, dice: List<Dice>, maxWounds: Int, diceLeft: Int = dice.size): Int {
    if (diceLeft == 0) {
        return if (total >= maxWounds) 1 else 0
    }
    var count = 0
    for (i in 1..dice[diceLeft -1].sides) {
        count += countFavorableOutcomes(total + i, dice, maxWounds, diceLeft - 1)
    }
    return count
}

