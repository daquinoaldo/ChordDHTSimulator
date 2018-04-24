object AutomatedMainBAK {

    @JvmStatic
    fun main(args: Array<String>) {

        // Configs
        val ms = intArrayOf(16, 32, 64) // max 64 = long, because Cytoscape can't manage only longs
        val ns = intArrayOf(1024, 4096, 8192, 16384, 32768) // 2^10 .. 2^15
        val qs = intArrayOf(1024, 4096, 8192, 16384, 32768, 65536)
        //////////

        val all = ms.size*ns.size
        var iNetwork = 0

        for (m in ms)
            for (n in ns) {
                println("Network ${++iNetwork} of $all: m=$m, n=$n")
                val coordinator = Coordinator(m, n)
                var iQuery = 0
                for (q in qs) {
                    println("\tquery${++iQuery} of ${qs.size}: m=$m, n=$n, q=$q")
                    coordinator.simulateQuery(q)
                }
            }
    }

}
