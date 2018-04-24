import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Random
import java.util.TreeMap

const val OUTPUT_FOLDER = "csv"

class Coordinator(private val m : Int, private val n : Int, plotTopology : Boolean = true,
                  private val LOG_LEVEL : Int = LogLevel.NO) {

    private val maxInt = BigInteger.valueOf(2L).pow(m) // 2^bit
    private val md = MessageDigest.getInstance("SHA-1")
    private val rand = Random()
    private var nodeIdentifierCollisions = 0
    private val nodes = TreeMap<BigInteger, Node>()
    private val crossedNodes = HashMap<BigInteger, Int>()
    private val outputDir = File("$OUTPUT_FOLDER/")

    private fun newId () : BigInteger {
        return BigInteger(md.digest(ByteBuffer.allocate(4).putInt(rand.nextInt(Integer.MAX_VALUE)).array()))
                .mod(maxInt)  // sha1 in int mod max num of m bit
    }

    private fun getSuccessor(id : BigInteger) : Node {
        // If null id is higher than all ids in the collection, so return the first (lowest) id.
        val result = nodes.higherEntry(id) ?: return nodes.firstEntry().value
        return result.value
    }

    private fun getPredecessor(id : BigInteger) : Node {
        // If null id is lower than all ids in the collection, so return the last (highest) id.
        val result = nodes.lowerEntry(id) ?: return nodes.lastEntry().value
        return result.value
    }

    private fun logData(text : String, level : Int = LogLevel.DEBUG) {
        if (level >= LOG_LEVEL) println("Coordinator: $text")
    }

    init {
        if (m < 0) throw IllegalArgumentException("Number m of bit of identifier must be positive.")
        if (n < 0) throw IllegalArgumentException("Number n of nodes must be positive.")

        // Init network
        generateNodes(n)
        buildFingerTable(m)

        // Create if not exist
        outputDir.mkdirs()

        // Plot network topology
        if (plotTopology) plotFingers("fingers_m${m}_n$n.csv")
    }

    // Routing statistics (must be called manually)
    fun simulateQuery(q: Int, plot : Boolean = true) : Pair<BigDecimal, BigDecimal> {
        if (q < 0) throw IllegalArgumentException("Number q of queries must be positive.")
        for (i in 0 until q) {
            val randomNode = getSuccessor(newId())
            val query = randomNode.findSuccessor(Query(newId()))
            crossedNodes[query.id] = query.crossedNode
        }
        if (plot) {
            plotCrossingQueries("cq_m${m}_n${n}_q$q.csv")
            plotCrossedNodes("cn_m${m}_n${n}_q$q.csv")
        }
        return Pair(avgCrossingQueries(), avgCrossedNodes())
    }

    private fun generateNodes(n : Int) {
        // I use random to do the sha of an every time different set of bytes starting from the same i,
        // otherwise it will loop in case of collision
        for (i in 0 until n) {
            var id = newId()
            // Avoid duplicates of ids (because of collisions caused by mod operator).
            while (nodes.containsKey(id)) {
                nodeIdentifierCollisions++
                logData("got a collision on id = $id", LogLevel.WARNING)
                id = newId()
            }
            logData("new node with id = $id", LogLevel.INFO)
            nodes[id] = Node(id, LOG_LEVEL)
        }
    }

    private fun buildFingerTable(m : Int) {
        for (node in nodes.values) {
            node.predecessor = getPredecessor(node.id)
            node.successor = getSuccessor(node.id)
            node.logData("predecessor = ${node.predecessor.id}", LogLevel.INFO)
            node.logData("successor = ${node.successor.id}", LogLevel.INFO)
            // Build the finger table of the node
            for (i in 1 .. m) {
                // n + 2^(i-1) with i in [1, m
                val target = (node.id + BigInteger.valueOf(2L).pow(i - 1)).mod(maxInt)
                node.fingers[target] = getSuccessor(target)
            }
        }
    }

    private fun plotFingers(pathname : String) {
        val bufferedWriter = File(outputDir, pathname).bufferedWriter()
        bufferedWriter.write("source,target\n")
        for (node in nodes.values) {
            bufferedWriter.append("${node.id},${node.predecessor.id}\n")
            bufferedWriter.append("${node.id},${node.successor.id}\n")
            for (target in node.fingers.values)
                bufferedWriter.append("${node.id},${target.id}\n")
        }
        bufferedWriter.close()
    }

    private fun plotCrossingQueries(pathname : String) {
        val bufferedWriter = File(outputDir, pathname).bufferedWriter()
        bufferedWriter.write("node,queries\n")
        for (node in nodes.values)
            bufferedWriter.append("${node.id},${node.crossingQueries}\n")
        bufferedWriter.flush()
        bufferedWriter.close()
    }

    private fun plotCrossedNodes(pathname : String) {
        val bufferedWriter = File(outputDir, pathname).bufferedWriter()
        bufferedWriter.write("node,queries\n")
        for (entry in crossedNodes.entries)
            bufferedWriter.append("${entry.key},${entry.value}\n")
        bufferedWriter.flush()
        bufferedWriter.close()
    }

    private fun avgCrossingQueries() : BigDecimal {
        var sum = BigInteger.valueOf(0L)
        for (node in nodes.values)
            sum += BigInteger.valueOf(node.crossingQueries.toLong())
        return BigDecimal(sum).divide(BigDecimal.valueOf(n.toLong()), 2, RoundingMode.HALF_UP)
    }

    private fun avgCrossedNodes() : BigDecimal {
        var sum = BigInteger.valueOf(0L)
        for (entry in crossedNodes.entries)
            sum += BigInteger.valueOf(entry.value.toLong())
        return BigDecimal(sum).divide(BigDecimal.valueOf(crossedNodes.size.toLong()), 2, RoundingMode.HALF_UP)
    }

}