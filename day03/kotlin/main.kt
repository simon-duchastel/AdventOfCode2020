import kotlin.io.readLine

enum class CompilationMode {
    IN_MEMORY_PART_1,
    NO_MEMORY_PART_1,
    IN_MEMORY_PART_2,
}
val COMPILATION_MODE = CompilationMode.IN_MEMORY_PART_1

fun main() {
    val numTrees = when (COMPILATION_MODE) {
        CompilationMode.IN_MEMORY_PART_1 -> {
            println("Using In Memory Part 1")
            inMemoryCountTrees(::readLine)
        }
        CompilationMode.NO_MEMORY_PART_1 -> {
            println("Using No Memory Part 1")
            readLine() // Read the first line to account for the fact that we should start at the first level of the slope
            sledDownSlope(::readLine, 3, 1)
        }
        CompilationMode.IN_MEMORY_PART_2 -> {
            println("Using In Memory Part 2")
            readLine() // Read the first line to account for the fact that we should start at the first level of the slope
            SlopeMap(::readLine).let { slope ->
                sledDownSlope(slope.SlopeTraverser()::goDownOneLevel, 1, 1) * 
                sledDownSlope(slope.SlopeTraverser()::goDownOneLevel, 3, 1) * 
                sledDownSlope(slope.SlopeTraverser()::goDownOneLevel, 5, 1) * 
                sledDownSlope(slope.SlopeTraverser()::goDownOneLevel, 7, 1) * 
                sledDownSlope(slope.SlopeTraverser()::goDownOneLevel, 1, 2)
            }
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
            (if (level[(horizontalPosition + horizontalTraversals) % level.size].isTree()) 1 else 0) + 
                sledDownSlope(
                    goDownOneLevel,
                    horizontalTraversals,
                    verticalTraversals,
                    horizontalPosition + horizontalTraversals
                )
            } ?: 0
    }

fun Char.isTree() = this == '#'

class SlopeMap(getNextLevel: () -> String?) {
    val levels: List<String> = loadAllLevels(getNextLevel)

    private fun loadAllLevels(getNextLevel: () -> String?, acc: List<String> = emptyList()): List<String> =
        getNextLevel()?.let { newLevel -> loadAllLevels(getNextLevel, acc.plus(newLevel)) } ?: acc

    inner class SlopeTraverser() {
        private var currentLevel = 0
        fun goDownOneLevel() = levels.getOrNull(currentLevel).also { currentLevel++ }
    }
}


// In-memory solution for Part 1

fun inMemoryCountTrees(getNextInputRow: () -> String?): Int {
    val slopeMap = HorizontalScrollingMatrix<Square>().loadAll { getNextInputRow()?.map(Char::toSquare) }
    
    var horizontalPosition = 0
    var numTrees = 0
    for (verticalPosition in 0 until slopeMap.height()) {
        if (slopeMap.get(horizontalPosition, verticalPosition) is Tree) numTrees++
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