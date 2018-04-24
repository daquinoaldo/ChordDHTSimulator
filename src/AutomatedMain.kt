import java.io.File

object AutomatedMain {

    @JvmStatic
    fun main(args: Array<String>) {

        // Configs
        val m = 64  // 64 = long, Cytoscape can manage only longs
        val ns = (1000..50000 step 1000)
        val q = 1000000
        //////////



        println("Generating networks with fixed number of identifier m = $m.")

        val bufferedWriter = File("stats_m$m.csv").bufferedWriter()
        bufferedWriter.write("n,avgCrossingQueries,avgCrossedNodes\n")

        var i = 0
        for (n in ns) {
            println("Network ${++i} of ${ns.count()}: n = $n")
            val coordinator = Coordinator(m, n, true)
            val (avgCrossingQueries, avgCrossedNodes) = coordinator.simulateQuery(q, false)
            bufferedWriter.append("$n,$avgCrossingQueries,$avgCrossedNodes\n")
        }

        bufferedWriter.close()
    }

}
