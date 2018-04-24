import java.util.Scanner

object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val m : Int
        val n : Int
        val q : Int

        if (args.size == 1 && (args[0] == "-a" || args[0] == "-t")) {
            AutomatedMain.main(args)
            return
        }

        if (args.size == 3) {
            m = Integer.parseInt(args[0])
            n = Integer.parseInt(args[1])
            q = Integer.parseInt(args[2])
        } else {
            val scanner = Scanner(System.`in`)
            print("How many bit for the identifier?\nm = ")
            m = scanner.nextInt()
            print("How many nodes?\nn = ")
            n = scanner.nextInt()
            print("How many queries?\nq = ")
            q = scanner.nextInt()
        }

        if (m < 0 || n < 0) {
            System.err.println("The number of bit of the identifiers and the number of nodes cannot be negative.")
            System.exit(1)
        }

        Coordinator(m, n).simulateQuery(q)
    }

}
