package model

enum class WeaponKeyword(val rules:List<Rule> = emptyList()) {
    ASSAULT(listOf(Rule.basicRule("No Advance And shoot").remove())),
    BLAST (
        listOf(
            Rule.always { target, _ ->
                DmgMod(plusShots = target.models.values.fold(0) { acc, value ->
                    acc + value
                } / 5 )
            }
        )
    ),
    HEAVY(
        listOf(
            Rule(
                condition = {_, shooter -> !shooter.hasMoved && !shooter.hasAdvanced},
                dmgMod = {_ ,_ -> DmgMod(plusToHit = 1)}
            )
        )
    ),
    INDIRECTFIRE,
    PISTOL,
    SUSTAINEDHITS;

    companion object {
        fun fromMultiString(input: String): List<WeaponKeyword> {
            return input.split(";", ignoreCase = true).filter { it.isNotEmpty() }.map { keyword ->
                fromString(keyword)
            }
        }

        fun fromString(input: String): WeaponKeyword {
            return WeaponKeyword.valueOf(input.trim().uppercase())
        }
    }
}
