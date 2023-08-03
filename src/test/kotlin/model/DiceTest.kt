package model


import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldNotBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import org.junit.jupiter.api.Test

class DiceTest {

    @Test
    fun diceShouldCalculateCorrectAverages() {
        Dice.D3.average shouldBeEqualComparingTo  BigDecimal(2.00)
        Dice.D6.average shouldBeEqualComparingTo  BigDecimal(3.50)
    }

    @Test
    fun diceShouldRollCorrectly(){
        repeat(100) {
            Dice.values().forEach {
                val roll = it.roll()
                roll shouldNotBeGreaterThanOrEqualTo it.sides + 1
                roll shouldBeGreaterThanOrEqualTo 1
            }
        }
    }

    @Test
    fun rollShouldCalculateCorrectProbabilitiesForNonRepeatedRolls() = runBlocking {
        val testCases = listOf(
            row(Roll.twoPlus, BigDecimal(5).divide(BigDecimal(6), 2, RoundingMode.HALF_UP)),
            row(Roll.threePlus, BigDecimal(4).divide(BigDecimal(6), 2, RoundingMode.HALF_UP)),
            row(Roll.fourPlus, BigDecimal(3).divide(BigDecimal(6), 2, RoundingMode.HALF_UP)),
            row(Roll.fivePlus, BigDecimal(2).divide(BigDecimal(6), 2, RoundingMode.HALF_UP)),
            row(Roll.sixPlus, BigDecimal(1).divide(BigDecimal(6), 2, RoundingMode.HALF_UP)),
        )

        forAll(*testCases.toTypedArray()){ enumValue: Roll, expected: BigDecimal ->
            enumValue.successProb() shouldBe expected
        }
    }

    @Test
    fun shouldCalculateCorrectProbabilitiesForRepeatableRolls() = runBlocking {
        val testCases = listOf(
            row(Roll.twoPlus, BigDecimal(35).divide(BigDecimal(36), 2, RoundingMode.HALF_UP)),
            row(Roll.threePlus, BigDecimal(32).divide(BigDecimal(36), 2, RoundingMode.HALF_UP)),
            row(Roll.fourPlus, BigDecimal(27).divide(BigDecimal(36), 2, RoundingMode.HALF_UP)),
            row(Roll.fivePlus, BigDecimal(20).divide(BigDecimal(36), 2, RoundingMode.HALF_UP)),
            row(Roll.sixPlus, BigDecimal(11).divide(BigDecimal(36), 2, RoundingMode.HALF_UP)),
        )

        forAll(*testCases.toTypedArray()){ enumValue: Roll, expected: BigDecimal ->
            enumValue.successProb(reroll = true) shouldBe expected
        }
    }

    @Test
    fun effectiveDamageValueShouldBeCalculatedCorrectly() = runBlocking{
        val testCases = listOf(
            row("D6 + 1", 4, BigDecimal(21).divide(BigDecimal(6))),
            row("D6 + D3 + 1", 6, BigDecimal(98).divide(BigDecimal(18), 2, RoundingMode.HALF_UP)),
            row("2", 1, BigDecimal(1) ),
            row ("D6", 6, BigDecimal(3.5)),
            row ("D6", 1, BigDecimal(1)),
            row ("D6 + 1 + D3", 3, BigDecimal(3)),
        )

        forAll(*testCases.toTypedArray()){damageString:String, wounds: Int, expected:BigDecimal ->
            Value.effectiveDamage(damageString, BigDecimal(wounds)) shouldBeEqualComparingTo  expected
        }
    }
}