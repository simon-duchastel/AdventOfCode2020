import kotlin.system.exitProcess
import kotlin.io.readLine
import java.io.File

enum class CompilationMode {
    FIND_PAIR_V1,
    FIND_TRIPLE_V1,
    FIND_PAIR_V2,
    FIND_TRIPLE_V2
}

val COMPILATION_MODE = CompilationMode.FIND_TRIPLE_V2

/**
 *  Returns two integers in the list which sum to 2020, or null
 *  if none are found
 */
fun findIntegerPairV1(ints: List<Int>): Pair<Int, Int>? {
    for (i in 0 until ints.size) {
        for (j in (i + 1) until ints.size) {
            if (ints[i] + ints[j] == 2020) {
                return Pair(ints[i], ints[j])
            }
        }
    }
    return null
}

/**
 *  Returns three integers in the list which sum to 2020, or null
 *  if none are found
 */
fun findIntegerTripleV1(ints: List<Int>): Triple<Int, Int, Int>? {
    for (i in 0 until ints.size) {
        for (j in (i + 1) until ints.size) {
            for (k in (j + 1) until ints.size) {
                if (ints[i] + ints[j] + ints[k] == 2020) {
                    return Triple(ints[i], ints[j], ints[k])
                }
            }
        }
    }
    return null
}

/**
 *  Returns two integers in the list which sum to 2020, or null
 *  if none are found
 */
fun findIntegerPairV2(ints: List<Int>): Pair<Int, Int>? {
    val sortedInts = ints.sortedBy({ it })
    for (i in 0 until (sortedInts.size - 1)) {    
        var j = i + 1
        do {
            val sum = sortedInts[i] + sortedInts[j]
            if (sum == 2020) {
                return Pair(sortedInts[i], sortedInts[j])
            }
            j++
        } while (sum <= 2020 && j < sortedInts.size) // sum strictly increases, stop when we surpass 2020
    }
    return null
}

/**
 *  Returns three integers in the list which sum to 2020, or null
 *  if none are found
 */
fun findIntegerTripleV2(ints: List<Int>): Triple<Int, Int, Int>? {
    val sortedInts = ints.sortedBy({ it })
    for (i in 0 until sortedInts.size) {
        for (j in (i + 1) until (sortedInts.size - 1)) {
            var k = j + 1
            do {
                val sum = sortedInts[i] + sortedInts[j] + sortedInts[k]
                if (sum == 2020) {
                    return Triple(sortedInts[i], sortedInts[j], sortedInts[k])
                }
                k++
            } while (sum <= 2020 && k < sortedInts.size) // sum strictly increases, stop when we surpass 2020
        }
    }
    return null
}

fun main(args : Array<String>) {
    val parsedInts: List<Int> = try {
        if (args.size == 0) readIntegers(::readLine)
        else if (args.size == 2 && args[0] == "-f") {
            val reader = File(args[1]).bufferedReader()
            readIntegers(reader::readLine)
        } else {
            exitWithMessage("Invalid command line arguments found. Use -f with a filename or no command line arguments")
        }
    } catch (ex: NumberFormatException) {
        exitWithMessage("Error parsing input: non-integer found")
    }

    when (COMPILATION_MODE) {
        CompilationMode.FIND_PAIR_V1 -> {
            println("Using algorithm v1")
            val (firstInt, secondInt) = findIntegerPairV1(parsedInts) ?: exitWithMessage("Unable to find answer")
            println("The product of $firstInt and $secondInt is ${firstInt * secondInt}")
        }
        CompilationMode.FIND_TRIPLE_V1 -> {
            println("Using algorithm v1")
            val (firstInt, secondInt, thirdInt) = findIntegerTripleV1(parsedInts) ?: exitWithMessage("Unable to find answer")
            println("The product of $firstInt, $secondInt, and $thirdInt is ${firstInt * secondInt * thirdInt}")
        }
        CompilationMode.FIND_PAIR_V2 -> {
            println("Using algorithm v2")
            val (firstInt, secondInt) = findIntegerPairV2(parsedInts) ?: exitWithMessage("Unable to find answer")
            println("The product of $firstInt and $secondInt is ${firstInt * secondInt}")
        }
        CompilationMode.FIND_TRIPLE_V2 -> {
            println("Using algorithm v2")
            val (firstInt, secondInt, thirdInt) = findIntegerTripleV2(parsedInts) ?: exitWithMessage("Unable to find answer")
            println("The product of $firstInt, $secondInt, and $thirdInt is ${firstInt * secondInt * thirdInt}")
        }
    }
}

fun readIntegers(getNextLine: () -> String?): List<Int> {
    var parsedInts = emptyList<Int>()
    var nextLine = getNextLine()
    while (nextLine != null) {
        parsedInts = parsedInts.plus(nextLine.toInt())
        nextLine = getNextLine()
    }
    return parsedInts
}

fun exitWithMessage(errorMessage: String): Nothing {
    println(errorMessage)
    exitProcess(1)
}