import kotlin.io.readLine
import kotlin.text.Regex

fun main() = BagGraph.constructBagGraph(::readLine)
                     .getNumInternalBags("shiny gold")
                     .let { println("$it bags must be held by a shiny gold bag") }

data class BagGraph private constructor(
    private val bags: MutableMap<String, Bag>
) {
    fun getNumInternalBags(bagName: String): Int =
        bags.get(bagName)?.getAllInternalBags()?.size ?: throw Exception("Bag $bagName does not exist in graph")

    fun bagsWhichCanContain(bagName: String): List<String> =
        bags.values.fold(emptyList<String>()) { list, bag ->
            if (bag.getAllInternalBags().find { it.name == bagName } != null) {
                list.plus(bag.name)
            } else {
                list
            } 
        }

    private fun lookupOrCreateBag(bagName: String): Bag =
        bags.get(bagName) ?: Bag(bagName).also { bags.put(it.name, it) }

    /**
     *  Construct the bag graph by continuously
     *  calling nextBag until null is returned,
     *  where nextBag returns the definition of
     *  bag.
     */
    companion object {
        fun constructBagGraph(nextBag: () -> String?): BagGraph {
            val graph = BagGraph(mutableMapOf())
            var bagLine = nextBag()
            while (bagLine != null) {
                val tokens = bagLine.split(Regex("( bags contain )|( bags?[,.]( )?)")).filter { !it.isEmpty() }//.also { foo -> foo.forEach { println(it) } }
                val bag = graph.lookupOrCreateBag(tokens[0])

                if (tokens[1] == "no other") { // to account for "NAME bags contain no other bags", which contain 0 bags
                    bagLine = nextBag()
                    continue 
                }
                
                tokens.drop(1).forEach { contains ->
                    val containsTokens = contains.split(" ", limit = 2)//.also { it.forEach { foo -> println("### $foo") } }
                    val containsBag = graph.lookupOrCreateBag(containsTokens[1])

                    bag.addContainsBag(Bag.Contains(containsBag, containsTokens[0].toInt()))
                }
                bagLine = nextBag()
            }
            return graph
        }
    }
}

data class Bag(
    val name: String,
    private val edges: MutableList<Contains> = mutableListOf()
) {
    fun addContainsBag(containsBag: Contains): Bag {
        edges.add(containsBag)
        return this
    }

    fun getAllInternalBags(): List<Bag> =
        edges.fold(emptyList<Bag>()) { list, edge ->
            list.plusNTimes(edge.count, edge.bag).plusNTimes(edge.count, edge.bag.getAllInternalBags())
        }

    data class Contains(
        val bag: Bag,
        val count: Int
    )
}

fun <T> List<T>.plusNTimes(n: Int, element: T): List<T> = 
    if (n > 0) this.plus(listOf(element).plusNTimes(n - 1, element)) else this

fun <T> List<T>.plusNTimes(n: Int, element: List<T>): List<T> = 
    if (n > 0) this.plus(element.plusNTimes(n - 1, element)) else this