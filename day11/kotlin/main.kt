import kotlin.io.readLine

fun main() = parseInitialLayout(::readLine)
                .getStabilizedNumberOfSeats(true, 5)
                .let { println("$it occupied seats in stable layout") }

data class FerryLayout(
    val layout: List<List<Seat>>
) {
    init {
        if (layout.size == 0) throw IllegalArgumentException("Minimum 1 row required")
        if (layout[0].size == 0) throw IllegalArgumentException("Minimum 1 column required")
    }

    val rows: Int
        get() = layout.size

    fun columns(row: Int): Int = layout.getOrNull(row)?.size ?: throw IllegalArgumentException("Row $row out of bounds")

    fun getNextLayout(withVisibility: Boolean = true, occupiedSeatThreshold: Int = 5): FerryLayout = 
        (if (withVisibility) ::getAdjacentVisibleSeats else ::getAdjacentSeats).let { adjacentSeatsFn ->
            FerryLayout(
                Array(rows) { row ->
                    Array(columns(row)) { column ->
                        when (layout[row][column]) {
                            is Seat.Floor -> Seat.Floor
                            is Seat.Occupied -> {
                                if (adjacentSeatsFn(row, column).count { it is Seat.Occupied } >= occupiedSeatThreshold) {
                                    Seat.Empty
                                } else {
                                    Seat.Occupied
                                }
                            }
                            is Seat.Empty -> {                        
                                if (adjacentSeatsFn(row, column).all(Seat?::isUnOccupied)) {
                                    Seat.Occupied
                                } else {
                                    Seat.Empty
                                }
                            }
                        }
                    }.toList()
                }.toList()
            )   
        }

    fun getOccupiedSeats(): Int = layout.fold(0) { acc, row ->
        acc + row.fold(0) { accCol, column -> accCol + if (column is Seat.Occupied) 1 else 0 }
    }

    override fun toString(): String = layout.fold("") { curString, row ->
        curString + "\n" + row.fold("") { curRowString, column ->
            curRowString + " " + column.toString()
        }.trim { it == ' ' }
    }.trim { it == '\n' }

    private fun getAdjacentSeats(row: Int, column: Int): List<Seat?> =
        FerryLayout.directions
                   .map { (rowDir, colDir) -> Pair(row + rowDir, column + colDir) }
                   .map { (rowToGet, colToGet) -> layout.getOrNull(rowToGet)?.getOrNull(colToGet) }

    private fun getAdjacentVisibleSeats(row: Int, column: Int): List<Seat?> =
        FerryLayout.directions
                   .map { dir -> getVisibleSeat(row, column, dir) }

    private fun getVisibleSeat(startRow: Int, startColumn: Int, direction: Pair<Int, Int>): Seat? =
        layout.getOrNull(startRow + direction.first)?.getOrNull(startColumn + direction.second)?.let {
            if (it is Seat.Floor) {
                getVisibleSeat(startRow + direction.first, startColumn + direction.second, direction)
            } else {
                it
            }
        }

    sealed class Seat {
        object Floor: Seat() { override fun toString() = "." }
        object Occupied: Seat() { override fun toString() = "#" }
        object Empty: Seat() { override fun toString() = "L" }
    }

    companion object {
        private val directions: List<Pair<Int, Int>> =
            listOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
                   Pair(0, -1), Pair(0, 1),
                   Pair(1, -1), Pair(1, 0), Pair(1, 1))
    }
}

fun parseInitialLayout(nextRow: () -> String?): FerryLayout {
    val layout: MutableList<List<FerryLayout.Seat>> = mutableListOf()
    do {
        val rowOfSeats = nextRow()?.map(Char::toSeat)?.also { layout.add(it) } 
    } while (rowOfSeats != null)
    return FerryLayout(layout.toList())
}

fun FerryLayout.getStabilizedNumberOfSeats(withVisibility: Boolean = true, occupiedSeatThreshold: Int = 5): Int = 
    getNextLayout(withVisibility, occupiedSeatThreshold).let { nextLayout ->
        if (nextLayout == this) {
            getOccupiedSeats()
        } else {
            nextLayout.getStabilizedNumberOfSeats(withVisibility, occupiedSeatThreshold)
        }
    }

fun Char.toSeat(): FerryLayout.Seat = when (this) {
    '.' -> FerryLayout.Seat.Floor
    'L' -> FerryLayout.Seat.Empty
    '#' -> FerryLayout.Seat.Occupied
    else -> throw IllegalArgumentException("Expected one of '.', 'L', or '#'")
}

fun FerryLayout.Seat?.isUnOccupied(): Boolean = this == null || this !is FerryLayout.Seat.Occupied