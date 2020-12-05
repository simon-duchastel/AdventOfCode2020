import kotlin.system.exitProcess
import kotlin.io.readLine
import java.io.File

/**
 *  Returns two integers in the list which sum to 2000, or null
 *  if none are found
 */
fun findIntegerPair2000(ints: List<Int>): Pair<Int, Int>? {
    for (i in 0 until ints.size) {
        for (j in (i+1) until ints.size) {
            if (ints[i] + ints[j] == 2000) {
                return Pair(ints[i], ints[j])
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

    val (firstInt, secondInt) = findIntegerPair2000(parsedInts) ?: exitWithMessage("Unable to find answer")
    println("The product of $firstInt and $secondInt is ${firstInt * secondInt}")
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