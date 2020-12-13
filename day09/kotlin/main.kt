import kotlin.io.readLine
import kotlin.ranges.IntRange

fun main() = readAllInputIntoList(::readLine)
                .let { list ->
                    val anamoly = findFirstNonSummableElement(list, 25)
                    val range = list.findContinguousRangeSummingTo(anamoly) ?: 
                        throw Exception("Unable to find continuguous range summing to ${anamoly}")
                    val sum = list.subList(range.start, range.endInclusive + 1).let {
                        it.smallest() + it.largest()
                    }
                    Triple(anamoly, range, sum)
                 }
                .let { (anamoly, range, sum) ->
                     println("${range.start} to ${range.endInclusive} sums to ${anamoly}")
                     println("Sum of smallest and largest numbers in this range is ${sum}")
                }


fun readAllInputIntoList(getNextLine: () -> String?): List<Long> =
    getNextLine()?.toLong()?.let {
        listOf(it) + readAllInputIntoList(getNextLine)
    } ?: emptyList()

fun findFirstNonSummableElement(fromList: List<Long>, bufferSize: Int): Long {
    val buffer = RingBuffer<Long>(bufferSize)
        .also {
            repeat (bufferSize) { index ->
                it.replaceNextElement(fromList.getOrNull(index) ?: throw Exception("Minimum $bufferSize elements required"))
            }
        }
    fromList.drop(bufferSize).forEach { nextElement ->
        if (!buffer.elementsCanSumTo(nextElement)) return nextElement
        buffer.replaceNextElement(nextElement)
    }
    throw IllegalArgumentException("All elements are the sum of 2 previous $bufferSize elements")
}

/**
 *  Data-structure representing a fixed-length array
 *  which can be added to infinitely, keeping only the
 *  last bufferSize elements
 */
class RingBuffer<T>(bufferSize: Int) {
    private val buffer: Array<BufferElement<T>> = Array<BufferElement<T>>(bufferSize) { BufferElement.Empty<T>() }
    private var curIndex: Int = 0 // The next element to be replaced

    val size: Int
        get() = buffer.size

    /**
     *  Replaces the oldest element in the buffer with nextElement
     */
    fun replaceNextElement(nextElement: T): RingBuffer<T> =
        this.also {
            buffer.set(curIndex, BufferElement.Exists(nextElement))
            curIndex = (curIndex + 1) % size
        }

    /**
     *  Gets the element at the given index, where the
     *  index counts from the oldest element to the newest.
     *  Returns null if the element is not set or if the index
     *  is out of bounds (note unset elements count as older
     *  than newly set elements, so get always returns null
     *  until the RingBuffer has been filled)
     */
    fun get(index: Int): T? =
        if (index !in 0 until size) {
            null
        } else {
            when (val retrieved = buffer[(curIndex + index) % size]) {
                is BufferElement.Exists -> retrieved.element
                is BufferElement.Empty -> null
            }
        }

    sealed class BufferElement<T> {
        data class Exists<T>(val element: T): BufferElement<T>()
        class Empty<T>: BufferElement<T>()
    }
}

/**
 *  Returns true if any 2 elements in the RingBuffer can sum to
 *  num, false otherwise
 */
fun RingBuffer<Long>.elementsCanSumTo(num: Long): Boolean {
    for (i in 0 until size) {
        val iThElement = get(i)
        if (iThElement == null || iThElement > num) continue

        for (j in (i + 1) until size) {
            val jThElement = get(j)
            if (jThElement != null && iThElement + jThElement == num) return true
        }
    }
    return false
}

/**
 *  Returns a range from startIndex to endIndex (inclusive) of all the
 *  elements in the RingBuffer which sum to num, or null if such indices
 *  cannot be found. The range is at least size 2 and at most size 
 */
fun List<Long>.findContinguousRangeSummingTo(num: Long): IntRange? {
    for (i in 0 until size) {
        for (j in (i + 1) until size) {
            if (subList(i, j + 1).sumAll() == num) return IntRange(i, j)
        }
    }
    return null
}

fun List<Long>.smallest(): Long = reduce { smallest, next -> if (next < smallest) next else smallest }
fun List<Long>.largest(): Long = reduce { largest, next -> if (next > largest) next else largest }

fun List<Long>.sumAll(): Long = fold(0L) { sum, next -> sum + next }
