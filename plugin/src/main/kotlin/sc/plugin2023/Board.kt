package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.*
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random
import sc.plugin2023.util.PluginConstants as Constants

/**
 * Klasse welche ein Spielbrett darstellt. Bestehend aus einem
 * zweidimensionalen Array aus Feldern
 *
 * @author soed
 */
@XStreamAlias(value = "board")
class Board(fields: TwoDBoard<Field> = generateFields()) : RectangularBoard<Field>(fields) {

    constructor(board: Board) : this(board.gameField.clone())

    override fun isValid(coordinates: Coordinates) =
        (coordinates.x + coordinates.y) % 2 == 0 &&
                coordinates.x >= 0 &&
                super.isValid(coordinates.copy(coordinates.x / 2))

    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    override operator fun get(x: Int, y: Int) =
        super.get(x / 2, y)

    /** Ersetzt die Fische des Feldes durch einen Pinguin.
     * @return Anzahl der ersetzten Fische. */
    operator fun set(position: Coordinates, team: Team?): Int {
        if (!isValid(position))
            outOfBounds(position)
        val field = gameField[position.y][position.x / 2]
        gameField[position.y][position.x / 2] = Field(penguin = team)
        return field.fish
    }

    fun possibleMovesFrom(pos: Coordinates) =
        Vector.DoubledHex.directions.flatMap { vector ->
            (1 until Constants.BOARD_SIZE).map {
                Move.run(pos, vector * it)
            }.takeWhile { getOrEmpty(it.to).fish > 0 }
        }

    /** Returns a list of the non-null filter outputs */
    fun <T> filterFields(filter: (Field, Coordinates) -> T?): Collection<T> =
        gameField.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, field ->
                filter(field, Coordinates.doubledHex(x, y))
            }
        }

    fun getPenguins() =
        filterFields { field, coordinates ->
            field.penguin?.let { Pair(coordinates, it) }
        }

    fun getOrEmpty(key: Coordinates?) = key?.let { getOrNull(it) } ?: Field()

    override val entries: Set<Map.Entry<Coordinates, Field>>
        get() = filterFields { f, coordinates -> FieldPosition(coordinates, f) }.toSet()

    override fun clone(): Board = Board(this)

    companion object {
        /** Generiert ein neues Spielfeld mit zufällig auf dem Spielbrett verteilten Fischen. */
        private fun generateFields(seed: Int = Random.nextInt()): TwoDBoard<Field> {
            val random = Random(seed)
            println("Board seed: $seed")
            val length = Constants.BOARD_SIZE
            val width = Constants.BOARD_SIZE
            val weightedInts =
                listOf(Field(0) to 0.1f, Field(1) to 0.2f, Field(2) to 0.4f, Field(3) to 0.2f, Field(4) to 0.1f)
            val totalSum = length * width
            val halfWidth = width / 2
            val halfEnforcedOnes = Constants.BOARD_SIZE / 2
            val arr: TwoDBoard<Field> = List(length) { MutableList(width) { Field(0) } }
            var countOne = 0

            for (i in 0 until length) {
                for (j in 0 until halfWidth) {
                    if (i * halfWidth + j < halfEnforcedOnes) {
                        arr[i][j] = Field(1)
                        countOne += 1
                        continue
                    }

                    val currentSum = arr.sumOf { it -> it.sumOf { it.fish } }
                    val notFilled = totalSum - (i * halfWidth + j)

                    val weightedSum = weightedInts.sumOf { it.second.toDouble() }
                    if (weightedSum.roundToInt() != 1) {
                        throw IllegalArgumentException("The sum of the probabilities must be 1. It is $weightedSum")
                    }

                    val lowestPossible = weightedInts.filter { it.first.fish >= (totalSum - currentSum) / notFilled }
                        .minOf { it.first.fish }
                    val highestPossible = min(totalSum - currentSum, weightedInts.maxOf { it.first.fish })

                    val possibleValues =
                        weightedInts.filter { it.first.fish in lowestPossible..highestPossible }.map { it.first.fish }
                    val possibleWeights =
                        weightedInts.filter { it.first.fish in lowestPossible..highestPossible }.map { it.second }

                    val value = random.nextFloat()
                    var cumulativeWeight = 0f
                    var index = 0
                    while (index < possibleValues.size && cumulativeWeight + possibleWeights[index] < value) {
                        cumulativeWeight += possibleWeights[index]
                        index++
                    }

                    arr[i][j] = Field(possibleValues[index])
                    countOne += if (arr[i][j].fish == 1) 1 else 0
                }
            }

            for (i in 0 until length) {
                for (j in 0 until halfWidth) {
                    val x = random.nextInt(length)
                    val y = random.nextInt(halfWidth)
                    arr[i][j] = arr[x][y].also { arr[x][y] = arr[i][j] }
                }
            }

            for (i in 0 until length) {
                for (j in 0 until halfWidth) {
                    arr[i] += arr[length - i - 1][halfWidth - j - 1]
                }
            }

            return arr.let {
                it + it.asReversed().map { list ->
                    MutableList(Constants.BOARD_SIZE) { index -> list[Constants.BOARD_SIZE - index - 1].clone() }
                }
            }

        }
    }
}
