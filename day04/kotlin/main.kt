import kotlin.text.Regex
import kotlin.io.readLine

fun main() = println("${checkPassports()} correct passports found (with validation)")

fun checkPassports(parse: String.() -> Password? = String::parseToPassword): Int = 
    getNextPassport()?.let { passwordString ->
        checkPassports(parse) + if (passwordString.parse() != null) 1 else 0
    } ?: 0

fun checkPassportsNoValidation() = checkPassports(String::parseToPasswordNoValidation)


fun getNextPassport(): String? = readLine()?.let { nextLine ->
        if (!nextLine.isEmpty()) {
            nextLine + getNextPassport()?.let{ " " + it }
        } else {
            ""
        }
    }

fun String.parseToPassword(): Password? = this.parseToPasswordNoValidation()?.validate()

fun String.parseToPasswordNoValidation(): Password? =
    this.split(" ").filter { !it.isEmpty() }.fold(Password.Builder()) { builder, token ->
        token.split(":").let { tokenPair ->
            when (tokenPair[0]) {
                "byr" -> builder.apply { birthYear = tokenPair[1] }
                "iyr" -> builder.apply { issueYear = tokenPair[1] }
                "eyr" -> builder.apply { expirationYear = tokenPair[1] }
                "hgt" -> builder.apply { height = tokenPair[1] }
                "hcl" -> builder.apply { hairColor = tokenPair[1] }
                "ecl" -> builder.apply { eyeColor = tokenPair[1] }
                "pid" -> builder.apply { passportId = tokenPair[1] }
                "cid" -> builder.apply { countryId = tokenPair[1] }
                else -> throw IllegalArgumentException("Unexpected token input found: ${tokenPair[0]}")
            }
        }
    }.buildOrNull()

data class Password(
    val birthYear: String,
    val issueYear: String,
    val expirationYear: String,
    val height: String,
    val hairColor: String,
    val eyeColor: String,
    val passportId: String,
    val countryId: String? = null
) {
    class Builder {
        var birthYear: String? = null
        var issueYear: String? = null
        var expirationYear: String? = null
        var height: String? = null
        var hairColor: String? = null
        var eyeColor: String? = null
        var passportId: String? = null
        var countryId: String? = null

        fun buildOrNull(): Password? {
            return Password(
                birthYear ?: return null,
                issueYear ?: return null, 
                expirationYear ?: return null,
                height ?: return null, 
                hairColor ?: return null,
                eyeColor ?: return null, 
                passportId ?: return null,
                countryId
            )
        }
    }
}

fun Password.validate(): Password? =
    this.validateBirthYear()
        ?.validateIssueYear()
        ?.validateExpirationYear()
        ?.validateHeight()
        ?.validateHairColor()
        ?.validateEyeColor()
        ?.validatePassportId()


fun Password.validateBirthYear(): Password? = if (birthYear.toIntOrNull() in 1920..2002) this else null
fun Password.validateIssueYear(): Password? = if (issueYear.toIntOrNull() in 2010..2020) this else null
fun Password.validateExpirationYear(): Password? = if (expirationYear.toIntOrNull() in 2020..2030) this else null
fun Password.validateHeight(): Password? =
    when {
        height.endsWith("cm") && height.substring(0 until height.length - 2).toIntOrNull() in 150..193 -> this
        height.endsWith("in") && height.substring(0 until height.length - 2).toIntOrNull() in 59..76 -> this
        else -> null
    }
fun Password.validateHairColor(): Password? = if (Regex("#[0-9a-f]{6}") matches hairColor) this else null
fun Password.validateEyeColor(): Password? =
    when (eyeColor) {
        "amb", "blu", "brn", "gry", "grn", "hzl", "oth" -> this
        else -> null
    }
fun Password.validatePassportId(): Password? = if (Regex("[0-9]{9}") matches passportId ) this else null