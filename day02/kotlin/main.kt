import kotlin.system.exitProcess
import kotlin.io.readLine
import java.io.File
import java.lang.IllegalArgumentException

/**
 *  Returns the number of correct passwords. Each invocation
 *  of getNextLine returns a new password line in the format 
 *  "min-max char: password" or null if there are no more password
 *  lines. The password is correct if it has at least min and
 *  at most max instances of the character char
 */
fun processPasswords(getNextLine: () -> String?): Int {
    var numCorrectPasswords = 0
    
    var nextToProcess = getNextLine()
    while (nextToProcess != null) {
        try {
            val processedString = nextToProcess.split(": ", " ", "-")
            val min: Int = processedString[0].toInt()
            val max: Int = processedString[1].toInt()
            val targetChar: Char = processedString[2].single()
            val password: String = processedString[3]

            var numTargetChars = 0
            password.map { if (it == targetChar) numTargetChars++ }
            if (numTargetChars >= min && numTargetChars <= max) numCorrectPasswords++            
        } catch (ex: NumberFormatException) {
            exitWithMessage("Unable to process password line: unable to parse integer")
        } catch (ex: IllegalArgumentException) {
            exitWithMessage("Unable to process password line: expected target character of length 1")
        }
        nextToProcess = getNextLine()
    }
    return numCorrectPasswords
}

fun main(args: Array<String>) {
    val numCorrect = if (args.size == 0) {
        processPasswords(::readLine)
    }
    else if (args.size == 2 && args[0] == "-f") {
        val reader = File(args[1]).bufferedReader()
        processPasswords(reader::readLine)
    } else {
        exitWithMessage("Invalid command line arguments found. Use -f with a filename or no command line arguments")
    }

    println("Number of valid passwords: $numCorrect")
}

fun exitWithMessage(errorMessage: String): Nothing {
    println(errorMessage)
    exitProcess(1)
}