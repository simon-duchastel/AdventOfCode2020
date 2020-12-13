import kotlin.io.readLine

fun main() = constructJoltPathFromInput(::readLine)
                .findAllPermutations()
                .let { println("${it} is the number of adapter permutations") }

fun constructJoltPathFromInput(input: () -> String?): List<Int> = 
    listOf(0).plus(readAllInputIntoList(input))
             .sortedBy { it }
             .let { it.plus(it.last() + 3) }

fun readAllInputIntoList(getNextLine: () -> String?): List<Int> =
    getNextLine()?.toInt()?.let {
        listOf(it) + readAllInputIntoList(getNextLine)
    } ?: emptyList()

/**
 *  This is a relatively hacky solution. It only works because
 *  there are no direct differences of 2 in the data (all differences
 *  of 2 are consecutive differences of 1) and the longest run is one
 *  of 5. However, runs longer than 5 can be accomodated by using the
 *  Tribonacci sequence
 */
fun List<Int>.findAllPermutations(): Long =
    this.foldIndexed(Pair(1, 1L)) { curIndex, (curRun, permutations), cur ->
        if (this.getOrNull(curIndex + 1) == cur + 1) {
            Pair(curRun + 1, permutations)
        } else {
            Pair(1, permutations * when (curRun) {
                1, 2 -> 1L
                3 -> 2L
                4 -> 4L
                5 -> 7L
                else -> throw Exception("Too large a sequence of differences of 1 found, unable to process")
            })
        }
    }.second

/**
 *  Returns a list of indices where the difference between
 *  the element at that index and the next element is of
 *  difference difference
 */
fun List<Int>.getDifferencesOf(difference: Int): List<Int> =
    first().let { firstElement ->
        drop(1).foldIndexed(Pair(firstElement, emptyList<Int>())) 
        { index, (prev, indices), next -> 
            Pair(next, if (next - prev == difference) indices + index else indices)
        }.second
    }