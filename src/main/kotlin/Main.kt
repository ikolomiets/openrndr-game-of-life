import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.RectangleBatchBuilder
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.shape.Rectangle

private fun RectangleBatchBuilder.drawCell(x: Int, y: Int, alive: Boolean) {
    strokeWeight = 0.1
    fill = if (alive) ColorRGBa.GREEN else ColorRGBa.BLACK
    rectangle(Rectangle(5 * x.toDouble(), 5 * y.toDouble(), 5.0, 5.0))
}

class RectangleBatchBuilderChangeListener(private val builder: RectangleBatchBuilder) : GameOfLife.ChangeListener {
    override fun onChange(x: Int, y: Int, alive: Boolean) = builder.drawCell(x, y, alive)
}

fun main() = application {
    configure {
        width = 1000
        height = 1000
        title = "Conway's Game of Life"
    }

    program {
        val gameOfLife = GameOfLife(200, 200)

        /*
            !Name: 119P4H1V0
            !Author: Dean Hickerson
            !The first c/4 orthogonal spaceship to be discovered
            !https://conwaylife.com/wiki/119P4H1V0
         */
        gameOfLife.parsePattern(
            160, 50, """
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

        /*
            !Name: 295P5H1V1
            !Author: Jason Summers
            !The first c/5 diagonal spaceship to be found. Discovered on November 22, 2000.
            !https://conwaylife.com/wiki/295P5H1V1
        */
        gameOfLife.parsePattern(
            120, 120, """
            |.............OO
            |.....OO....OO.O.O
            |....OOO....OOOO
            |...OO......OO.....O
            |..OO..OO...O..O..O
            |.OO.....O.......O..OO
            |.OO.O...OOOO
            |....O...OO..OO.O
            |.....OOO....O.O
            |......OO...OO..O
            |......O.....O
            |.OOOO.O..O..O...O
            |.OOO...OOOOO..OOOOOOO.O
            |O.O....O..........O..OO
            |OOO.O...O...O.....OOO
            |.......O.O..O.......OO
            |.O...O.....OO........OO..O.O
            |....O.......O........OOO.O.OOO
            |...O........OOO......O....O
            |.....O......O.O.....O.O
            |.....O......O.OO...O....O
            |.............O.OOOO...O.....O..O
            |............OO..OO.O.O...O.OOO
            |.................O......O..OOO...OOO
            |....................O..O......OO
            |................OO....O..O..........OO
            |..................O.............O...O
            |................OO....OO........O
            |.................O...OOO........O.O.O.O
            |.................O....OO........O.....OO
            |........................O........O..OOO
            |.....................O..O........O........O
            |..........................OOOO........OO...O
            |.......................O......OO......OO...O
            |.......................O....O............O
            |.......................O...............O
            |.........................OO.O.O.......O..O
            |.........................O....O.........OOO
            |............................OOO.OO..O...O...O.OO
            |.............................O..OO.O.....O...O..O
            |.....................................OO..O...O
            |..................................O.OO.OO.O..OO...O
            |...............................O.....O...O.......O.O
            |................................OO............OO...O
            |......................................O.......OO
            |.......................................OOO...OO..O
            |......................................O..O.OOO
            |......................................O....OO
            |.......................................O
            |..........................................O..O
            |.........................................O
            |..........................................OO
            """
        )

        var generations = 1
        var lastGeneration = false
        var lastRender = 0.0

        extend(ScreenRecorder())
        extend {
            val fps = 1 / (seconds - lastRender)
            drawer.text("Generations: %d; FPS: %.0f".format(generations, fps), 20.0, 40.0)

            val batchBuilder = RectangleBatchBuilder(drawer)

            gameOfLife.getLiveCells().forEach { batchBuilder.drawCell(it.first, it.second, true) }
            if (!lastGeneration) {
                val changeListener = RectangleBatchBuilderChangeListener(batchBuilder)
                lastGeneration = !gameOfLife.nextGeneration(changeListener)
                generations++
            }

            drawer.rectangles(batchBuilder.batch())
            lastRender = seconds

            if (generations == 900)
                application.exit()
        }
    }
}