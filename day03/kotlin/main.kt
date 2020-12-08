import kotlin.io.readLine

enum class CompilationMode {
    IN_MEMORY_PART_1,
    NO_MEMORY_PART_1,
    IN_MEMORY_PART_2,
}
val COMPILATION_MODE = CompilationMode.NO_MEMORY_PART_1

fun main() {
    val numTrees = when (COMPILATION_MODE) {
        CompilationMode.IN_MEMORY_PART_1 -> {
            println("Using In Memory Part 1")
            inMemoryCountTrees(::readLine)
        }
        CompilationMode.NO_MEMORY_PART_1 -> {
            println("Using No Memory Part 1")
            sledDownSlope(::readLine, 3, 1)
        }
        CompilationMode.IN_MEMORY_PART_2 -> {
            println("Using In Memory Part 2")
            sledDownSlope(::readLine, 3, 1)
        }
    }
    println("$numTrees trees encountered")
}

fun sledDownSlope(goDownOneLevel: () -> String?,
                horizontalTraversals: Int,
                verticalTraversals: Int, 
                horizontalPosition: Int = 0): Int = 
    repeat(verticalTraversals - 1) { goDownOneLevel() ?: return@sledDownSlope 0 }.let {
        goDownOneLevel()?.toCharArray()?.let { level ->
            (if (level[horizontalPosition % level.size].isTree()) 1 else 0) + 
                sledDownSlope(
                    goDownOneLevel,
                    horizontalTraversals,
                    verticalTraversals,
                    horizontalPosition + horizontalTraversals
                )
            } ?: 0
    }

fun Char.isTree() = this == '#'


// In-memory solution for Part 1

fun inMemoryCountTrees(getNextInputRow: () -> String?): Int {
    val map = HorizontalScrollingMatrix<Square>().loadAll { getNextInputRow()?.map(Char::toSquare) }
    
    var horizontalPosition = 0
    var numTrees = 0
    for (verticalPosition in 0 until map.height()) {
        if (map.get(horizontalPosition, verticalPosition) is Tree) numTrees++
        horizontalPosition += 3
    }
    return numTrees
}

class HorizontalScrollingMatrix<T>() {
    val rows = mutableListOf<List<T>>()
    val width: Int by lazy { rows[0].size }

    fun height() = rows.size

    fun loadAll(nextRow: () -> List<T>?): HorizontalScrollingMatrix<T> {
        var row: List<T>? = nextRow() ?: throw IllegalArgumentException("At least 1 row necessary for matrix")
        while (row != null) {
            rows.add(row)
            row = nextRow()
        }
        return this
    }

    fun get(horizontal: Int, vertical: Int): T {
        return rows[vertical][horizontal % width]
    }
}

sealed class Square
object Open: Square()
object Tree: Square()

fun Char.toSquare(): Square {
    return when (this) {
        '.' -> Open
        '#' -> Tree
        else -> throw IllegalArgumentException("Unable to parse square from token $this: '#' or '.' expected")
    }
}