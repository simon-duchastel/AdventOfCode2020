import kotlin.io.readLine
import kotlin.math.abs

fun main() = println("${readAllAndFindMySeatId(7, 3)} is my seat id")

fun readAndFindLargestSeatIdV1(numRowChars: Int, numColChars: Int): Int? = 
    readLine()?.calculateSeatId(numRowChars, numColChars)?.let { seatId ->
        seatId takeLargestBetween readAndFindLargestSeatIdV1(numRowChars, numColChars)
    }

fun readAndFindLargestSeatIdV2(numRowChars: Int, numColChars: Int): Int = 
    readAllSeatIds(numRowChars, numColChars)
        .sortedBy { it }
        .last()

fun readAllAndFindMySeatId(numRowChars: Int, numColChars: Int): Int =
    readAllSeatIds(numRowChars, numColChars)
        .sortedBy { it }
        .findMySeat()

fun readAllSeatIds(numRowChars: Int, numColChars: Int): List<Int> = 
    readLine()?.calculateSeatId(numRowChars, numColChars)?.let { seatId ->
        listOf(seatId) + readAllSeatIds(numRowChars, numColChars)
    } ?: emptyList()

fun List<Int>.findMySeat(): Int = 
    if (abs(this.get(1) - this.get(0)) > 1) {
        this.get(0) + 1
     } else {
         this.slice(1 until this.size).findMySeat()
     }

infix fun Int.takeLargestBetween(other: Int?) = if (other == null || this > other) this else other

fun String.calculateSeatId(numRowChars: Int, numColChars: Int): Int =
    this.foldIndexed(0) { index, acc, character ->
        when (character) {
            'F' -> acc 
            'B' -> acc + 8 * (2 pow (numRowChars - index - 1))
            'L' -> acc
            'R' -> acc + (2 pow (numColChars - index + numRowChars - 1))
            else -> throw IllegalArgumentException("Unexpected character found in boarding pass: $character")
        }
    }

infix fun Int.pow(other: Int): Int = if (other > 0) this * this.pow(other - 1) else 1