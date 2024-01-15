import java.util.*

class GameOfLife(private val width: Int, private val height: Int) {

    private val liveCells: MutableSet<Int> = TreeSet()

    private val bitSet = BitSet(width * height)

    private fun indexFromXY(x: Int, y: Int): Int {
        val modX = x % width
        val modY = y % height
        return (if (modX < 0) (width + modX) else modX) + width * (if (modY < 0) (height + modY) else modY)
    }

    private fun indexToXY(index: Int): Pair<Int, Int> = Pair(index % width, index / width)

    fun get(index: Int): Boolean = bitSet.get(index)

    fun get(x: Int, y: Int) = get(indexFromXY(x, y))

    fun set(index: Int, value: Boolean) {
        bitSet.set(index, value)
        if (value) liveCells.add(index) else liveCells.remove(index)
    }

    fun set(x: Int, y: Int, value: Boolean) = set(indexFromXY(x, y), value)

    private fun neighbours(x: Int, y: Int): List<Int> = listOf(
        indexFromXY(x - 1, y - 1), indexFromXY(x, y - 1), indexFromXY(x + 1, y - 1),
        indexFromXY(x - 1, y), indexFromXY(x + 1, y),
        indexFromXY(x - 1, y + 1), indexFromXY(x, y + 1), indexFromXY(x + 1, y + 1)
    )

    private fun countNeighbours(x: Int, y: Int): Int = neighbours(x, y)
        .map { get(it) }
        .sumOf { (if (it) 1 else 0).toInt() }

    fun getLiveCells(): List<Pair<Int, Int>> = liveCells.map { indexToXY(it) }

    interface ChangeListener {
        fun onChange(x: Int, y: Int, alive: Boolean)
    }

    companion object {
        private val NOOP_CHANGE_LISTENER = object : ChangeListener {
            override fun onChange(x: Int, y: Int, alive: Boolean) {
            }
        }
    }

    fun nextGeneration(listener: ChangeListener = NOOP_CHANGE_LISTENER): Boolean {
        val born = mutableListOf<Int>()
        val died = mutableListOf<Int>()
        liveCells
            .map { indexToXY(it) }
            .flatMap { neighbours(it.first, it.second).plus(indexFromXY(it.first, it.second)) }
            .toSet()
            .onEach {
                val alive = get(it)
                val (x, y) = indexToXY(it)
                val neighbours = countNeighbours(x, y)
                when {
                    alive && (neighbours < 2 || neighbours > 3) -> {
                        listener.onChange(x, y, false)
                        died.add(it)
                    }

                    !alive && neighbours == 3 -> {
                        listener.onChange(x, y, true)
                        born.add(it)
                    }
                }
            }

        born.forEach { set(it, true) }
        died.forEach { set(it, false) }

        return born.isNotEmpty() || died.isNotEmpty()
    }

    fun parsePattern(x: Int, y: Int, pattern: String) = pattern.lines().forEachIndexed { row, line ->
        line.toList().forEachIndexed { col, ch -> if (ch == 'O') set(x + col, y + row, true) }
    }
}

fun main() {
    val gameOfLife = GameOfLife(100, 100)

    gameOfLife.parsePattern(
        10, 10, """
            |.................................O
            |................O...............O.O
            |......O.O......O.....OO........O
            |......O....O....O.OOOOOO....OO
            |......O.OOOOOOOO..........O..O.OOO
            |.........O.....O.......OOOO....OOO
            |....OO.................OOO.O
            |.O..OO.......OO........OO
            |.O..O
            |O
            |.O..O
            |.O..OO.......OO........OO
            |....OO.................OOO.O
            |.........O.....O.......OOOO....OOO
            |......O.OOOOOOOO..........O..O.OOO
            |......O....O....O.OOOOOO....OO
            |......O.O......O.....OO........O
            |................O...............O.O
            |.................................O
            |""".trimMargin().trimEnd()
    )

    var generations = 0
    val start = System.currentTimeMillis()
    while (gameOfLife.nextGeneration()) {
        generations++
        if (generations % 10000 == 0) {
            val elapsed = System.currentTimeMillis() - start
            val gps = generations * 1000.0 / elapsed
            println("$generations (%.2f)".format(gps))
        }
    }
}