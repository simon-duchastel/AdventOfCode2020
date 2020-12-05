import kotlin.system.exitProcess
import kotlin.io.readLine
import java.io.File

enum class CompilationMode {
    FIND_PAIR,
    FIND_TRIPLE
}

val COMPILATION_MODE = CompilationMode.FIND_TRIPLE

/**
 *  Returns two integers in the list which sum to 2020, or null
 *  if none are found
 */
fun findIntegerPair(ints: List<Int>): Pair<Int, Int>? {
    for (i in 0 until ints.size) {
        for (j in (i+1) until ints.size) {
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
fun findIntegerTriple(ints: List<Int>): Triple<Int, Int, Int>? {
    for (i in 0 until ints.size) {
        for (j in (i+1) until ints.size) {
            for (k in (j+1) until ints.size) {
                if (ints[i] + ints[j] + ints[k] == 2020) {
                    return Triple(ints[i], ints[j], ints[k])
                }
            }
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
        CompilationMode.FIND_PAIR -> {
            val (firstInt, secondInt) = findIntegerPair(parsedInts) ?: exitWithMessage("Unable to find answer")
            println("The product of $firstInt and $secondInt is ${firstInt * secondInt}")
        }
        CompilationMode.FIND_TRIPLE -> {
            val (firstInt, secondInt, thirdInt) = findIntegerTriple(parsedInts) ?: exitWithMessage("Unable to find answer")
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