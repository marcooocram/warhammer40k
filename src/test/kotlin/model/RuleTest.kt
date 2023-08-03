package model

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RuleTest {

    @Test
    fun remove() {

        val rule = Rule(
            dmgMod = { _,_  -> DmgMod() },
            condition = { _, _ -> true}
        )

        rule.remove() shouldBe rule
       // rule.remove() shouldBeEqualToComparingFields rule
    }
}