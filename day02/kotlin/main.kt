import kotlin.system.exitProcess
import kotlin.io.readLine
import java.io.File
import java.lang.IllegalArgumentException

enum class CompilationMode {
    COUNT_VERIFICATION,
    POSITIONAL_VERIFICATION
}
val COMPILATION_MODE = CompilationMode.COUNT_VERIFICATION

/**
 *  Returns the number of correct passwords. Each invocation
 *  of getNextLine returns a new password line in the format 
 *  "first-second char: password" or null if there are no more
 *  password lines.
 */
fun processPasswords(getNextLine: () -> String?): Int {
    var numCorrectPasswords = 0
    
    var nextToProcess = getNextLine()
    while (nextToProcess != null) {
        try {
            val processedString = nextToProcess.split(": ", " ", "-")
            val first: Int = processedString[0].toInt()
            val second: Int = processedString[1].toInt()
            val targetChar: Char = processedString[2].single()
            val password: String = processedString[3]

            val isCorrect = when (COMPILATION_MODE) {
                CompilationMode.COUNT_VERIFICATION -> verifyPasswordByCount(password, first, second, targetChar)
                CompilationMode.POSITIONAL_VERIFICATION -> verifyPasswordByPosition(password, first, second, targetChar)
            }
            if (isCorrect) numCorrectPasswords++            
        } catch (ex: NumberFormatException) {
            exitWithMessage("Unable to process password line: unable to parse integer")
        } catch (ex: IllegalArgumentException) {
            exitWithMessage("Unable to process password line: expected target character of length 1")
        }
        nextToProcess = getNextLine()
    }
    return numCorrectPasswords
}

/**
 *  Returns true if the password is correct, false otherwise. The password is correct
 *  if it has at least min and at most max instances of the character targetChar
 */
fun verifyPasswordByCount(password: String, min: Int, max: Int, targetChar: Char): Boolean {
    var numTargetChars = 0
    password.map { if (it == targetChar) numTargetChars++ }
    return numTargetChars >= min && numTargetChars <= max
}

/**
 *  Returns true if the password is correct, false otherwise. The password is correct
 *  if either the character at position firstPosition or the character at position
 *  secondPosition match targetChar, but not if both match.
 */
fun verifyPasswordByPosition(password: String, firstPosition: Int, secondPosition: Int, targetChar: Char): Boolean {
    val firstPositionMatches = password.getOrNull(firstPosition - 1) == targetChar
    val secondPositionMatches = password.getOrNull(secondPosition - 1) == targetChar
    return firstPositionMatches xor secondPositionMatches
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