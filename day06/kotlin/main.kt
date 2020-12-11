import kotlin.io.readLine

fun main() = println("${getSumCommonAnswers()} unique answers across all groups")

fun getSumUniqueAnswers(): Int = readAllGroups().fold(0) { acc, group -> acc + group.uniqueGroupAnswers().size }
fun getSumCommonAnswers(): Int = readAllGroups().fold(0) { acc, group -> acc + group.commonGroupAnswers().size }

fun readAllGroups(): List<CustomsGroup> = readCustomsGroup()?.let {
    listOf(it) + readAllGroups()
} ?: emptyList()

fun readCustomsGroup(): CustomsGroup? {
    var nextLine = readLine()
    var customsForms = mutableListOf<CustomsForm>()
    while (nextLine != null && !nextLine.isEmpty()) {
        customsForms.add(nextLine.toCustomsForm())
        nextLine = readLine()
    }
    return if (customsForms.isEmpty()) null else CustomsGroup(customsForms)
}

/**
 *  Represents the customs form for 1 person
 */
data class CustomsForm(
    val answers: List<CustomsAnswer>
)
typealias CustomsAnswer = Char

fun String.toCustomsForm() = this.toList().let { CustomsForm(it) }

/**
 *  Represents the customs answers for a group of
 *  people
 */
data class CustomsGroup(
    val forms: List<CustomsForm>
)

fun CustomsGroup.uniqueGroupAnswers(): List<CustomsAnswer> = forms.flatMap { it.answers }.distinct()
fun CustomsGroup.commonGroupAnswers(): List<CustomsAnswer> = forms.removeFirst()
    .let { (first, rest) ->
        rest.fold(first.answers.toSet()) { acc, customsForm -> 
            acc.intersect(customsForm.answers.toSet()) 
        }
    }.toList()

fun <T> List<T>.removeFirst(): Pair<T, List<T>> = this.getOrNull(0)?.let { first ->
    Pair(first, this.drop(1))
} ?: throw IllegalArgumentException("List must contain at least 1 element")