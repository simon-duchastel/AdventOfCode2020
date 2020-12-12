import kotlin.io.readLine

fun main() = readAllInstructions()
                .let { ProgramState(it) }
                .runProgram()
                .let { println("${it.accumulator} is the value of the accumulator after termination or a loop") }

fun readAllInstructions(): List<Instruction> = readLine()?.toInstruction()?.let { instruction ->
    listOf(instruction).plus(readAllInstructions())
} ?: emptyList()

data class ProgramState(
    val instructions: List<Instruction>,
    val curInstruction: Int = 0,
    val accumulator: Int = 0,
    val hasExecutedInstruction: List<Boolean> = instructions.map { false }
) {
    /**
     *  Returns a new program state that terminates by changing exactly
     *  one nop instruction to a jmp or vice-versa.
     */
    fun fixProgram(): ProgramState = 
        instructions.foldIndexed(this) { index, program, nextInst ->
            if (program != this) {
                program
            } else {
                when (nextInst) {
                    is Instruction.Nop -> instructions.replaceIndex(index, Instruction.Jmp(nextInst.useless))
                    is Instruction.Jmp -> instructions.replaceIndex(index, Instruction.Nop(nextInst.dist))
                    is Instruction.Acc -> null
                }?.let { newInstructions -> this.copy(instructions = newInstructions) }
                 ?.runProgram()
                 ?.let { if (it.hasTerminated()) it else program } ?: program
            }
        }

    /**
     *  Executes the next instruction and returns the resultant program
     *  state, or itself if a loop is detected / the program has terminated
     */
    fun executeInstruction(): ProgramState =
        if (hasTerminated() || hasExecutedInstruction[curInstruction]) {
            this
        } else {
            when (val inst = instructions[curInstruction]) {
                is Instruction.Nop -> generateNextState(curInstruction + 1, accumulator)
                is Instruction.Acc -> generateNextState(curInstruction + 1, accumulator + inst.by)
                is Instruction.Jmp -> generateNextState(curInstruction + inst.dist, accumulator)
            }
        }

    fun hasTerminated(): Boolean = curInstruction !in 0 until instructions.size

    private fun generateNextState(nextInstruction: Int, nextAccumulator: Int): ProgramState =
        ProgramState(
            instructions, 
            nextInstruction,
            nextAccumulator, 
            hasExecutedInstruction.replaceIndex(curInstruction, true)
        )

    /**
     * Runs the program until a loop has been detected
     */
    fun runProgram(): ProgramState =
        executeInstruction().let { newProgram ->
            if (newProgram != this) newProgram.runProgram() else this
        }
}

fun String.toInstruction(): Instruction {
    val tokens = split(" ")
    return when (tokens[0]) {
        "nop" -> Instruction.Nop(instructionValueToInt(tokens[1]))
        "acc" -> Instruction.Acc(instructionValueToInt(tokens[1]))
        "jmp" -> Instruction.Jmp(instructionValueToInt(tokens[1]))
        else -> throw IllegalArgumentException("$this is an invalid instruction")
    }
}

fun instructionValueToInt(value: String): Int = 
    value.drop(1).toInt() * when (value.first()) {
        '-' -> -1
        '+' -> 1
        else -> throw IllegalArgumentException("$value must begin with '+' or '-'")
    }

sealed class Instruction {
    data class Acc(val by: Int): Instruction()
    data class Jmp(val dist: Int): Instruction()
    data class Nop(val useless: Int): Instruction()
}

fun <T> List<T>.replaceIndex(indexToReplace: Int, replaceWith: T): List<T> =
    mapIndexed { index, elem -> if (indexToReplace == index) replaceWith else elem }