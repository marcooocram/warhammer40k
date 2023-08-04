package model

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RuleTest {

    @Test
    fun remove() {

        val rule = Rule(
            dmgMod = { _,_  -> DmgMod() },
            condition = { _, _ -> true}
        )

        rule.remove() shouldBe rule
        rule.remove().dmgMod shouldBe rule.dmgMod
        rule.remove().condition shouldBe rule.condition
        rule.removed shouldBe false
        rule.remove().removed shouldBe true
    }
}