import java.math.BigInteger
import java.util.TreeMap

class Node (val id : BigInteger, private val LOG_LEVEL : Int = LogLevel.INFO) {

    var predecessor = this
    var successor = this
    var fingers = TreeMap<BigInteger, Node>()
    var crossingQueries = 0

    fun logData(text : String, level : Int = LogLevel.DEBUG) {
        if (level >= LOG_LEVEL) println("Node $id: $text")
    }

    fun findSuccessor(query: Query): Query {
        // Update stats
        crossingQueries++
        query.crossedNode++
        // First I check if I am the successor,
        if (predecessor.id < query.id && query.id <= this.id) {
            query.destinationNode = this
            return query
        }
        // else check if the successor is my successor,
        if (this.id < query.id && query.id <= successor.id) {
            query.destinationNode = successor
            return query
        }
        // SPECIAL CASE 1: I am the first node (so my predecessor has a higher id then mine)
        // and the query id stay between our ids (less then my or greater then my predecessor's)
        if (predecessor.id > this.id && (query.id <= this.id || query.id > predecessor.id)) {
            query.destinationNode = this
            return query
        }
        // SPECIAL CASE 2: I am the last node (so my successor has a lower id then mine)
        // and the query id stay between our ids (greater then my or less then my predecessor's)
        if (successor.id < this.id && (query.id > this.id || query.id <= successor.id)) {
            query.destinationNode = successor
            return query
        }
        // Finally, I ask to the closest preceding node in my finger table to find the successor for me.
        val cpn = closestPrecedingNode(query.id)
        if(cpn == this)  {
            query.destinationNode = this
            return query
        }
        return cpn.findSuccessor(query)
    }

    private fun closestPrecedingNode(id : BigInteger) : Node {
        if (fingers.isEmpty()) throw RuntimeException("Finger table of node ${this.id} is empty.")
        // If null id is lower than all ids in the collection, so return the last (highest) id.
        val result = fingers.lowerEntry(id) ?: return fingers.lastEntry().value
        return result.value
    }
}


