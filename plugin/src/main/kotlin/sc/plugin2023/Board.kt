package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import kotlin.math.min
import kotlin.math.roundToInt
import sc.framework.deepCopy
import kotlin.random.Random
import sc.plugin2023.util.PenguinConstants as Constants

/** Spielbrett aus einem zweidimensionalen Array aus Feldern. */
@XStreamAlias(value = "board")
class Board(
    @XStreamImplicit(itemFieldName = "row")
    override val gameField: MutableTwoDBoard<Field> = generateFields(),
): RectangularBoard<Field>(), IBoard {
    
    constructor(board: Board): this(board.gameField.deepCopy())
    
    override fun isValid(coordinates: Coordinates) =
            (coordinates.x + coordinates.y) % 2 == 0 &&
            coordinates.x >= 0 &&
            super.isValid(coordinates.copy(coordinates.x / 2))
    
    /** Prüft, ob auf dieser [position] bereits eine Spielfigur ist. */
    fun isOccupied(position: Coordinates): Boolean =
            this[position].isOccupied
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    override operator fun get(x: Int, y: Int) =
            super.get(x / 2, y)
    
    /** Ersetzt die Fische des Feldes durch einen Pinguin.
     * @return Anzahl der ersetzten Fische. */
    operator fun set(position: Coordinates, team: Team?): Int {
        if(!isValid(position))
            outOfBounds(position)
        val field = gameField[position.y][position.x / 2]
        gameField[position.y][position.x / 2] = Field(penguin = team)
        return field.fish
    }
    
    fun possibleMovesFrom(pos: Coordinates) =
            HexDirection.values().flatMap { direction ->
                (1 until Constants.BOARD_SIZE).map {
                    Move.run(pos, direction.vector * it)
                }.takeWhile { getOrEmpty(it.to).fish > 0 }
            }
    
    /** Returns a list of the non-null filter outputs */
    fun <T> filterFields(filter: (Field, Coordinates) -> T?): Collection<T> =
            gameField.flatMapIndexed { y, row ->
                row.mapIndexedNotNull { x, field ->
                    filter(field, Coordinates(x * 2 + y % 2, y))
                }
            }
    
    fun getPenguins() =
            filterFields { field, coordinates ->
                field.penguin?.let { Pair(coordinates, it) }
            }
    
    fun getOrEmpty(key: Coordinates?) = key?.let { getOrNull(it) } ?: Field()
    
    override val entries: Set<Positioned<Field>>
        get() = filterFields { f, coordinates -> Positioned(coordinates, f) }.toSet()
    
    override fun clone(): Board = Board(this)
    
    companion object {
        /** Generiert ein neues Spielfeld mit zufällig auf dem Spielbrett verteilten Fischen. */
        private fun generateFields(seed: Int = Random.nextInt()): MutableTwoDBoard<Field> {
            val random = Random(seed)
            println("Board seed: $seed")
            
            var remainingFish = Constants.BOARD_SIZE * Constants.BOARD_SIZE
            var maxholes = 5
            // Pro Hälfte 32 Felder, mind. 27 Schollen
            // Maximal (64-20)/2 = 22 2-Fisch-Schollen,
            // also immer mindestens 5 1-Fisch-Schollen pro Seite
            return Array(Constants.BOARD_SIZE / 2) {
                Array(Constants.BOARD_SIZE) {
                    val rand = random.nextInt(remainingFish)
                    if(rand < maxholes) {
                        maxholes--
                        Field()
                    } else {
                        val fish = (rand - maxholes) / 20 + 1
                        remainingFish -= fish
                        Field(fish)
                    }
                }
            }.let {
                it + it.reversedArray().map { list ->
                    Array(Constants.BOARD_SIZE) { index -> list[Constants.BOARD_SIZE - index - 1].deepCopy() }
                }
            }
        }
        
        private fun generateFieldsRandom(seed: Int = Random.nextInt()): MutableTwoDBoard<Field> {
            val random = Random(seed)
            println("Board seed: $seed")
            
            val length = Constants.BOARD_SIZE
            val width = Constants.BOARD_SIZE
            val weightedInts =
                listOf(Field(0) to 0.1f, Field(1) to 0.2f, Field(2) to 0.4f, Field(3) to 0.2f, Field(4) to 0.1f)
            val totalSum = length * width
            val halfWidth = width / 2
            val halfEnforcedOnes = Constants.BOARD_SIZE / 2
            val fields: TwoDBoard<Field> = Array(length) { Array(width) { Field(0) } }
            var countOne = 0
            
            for(i in 0 until length) {
                for(j in 0 until halfWidth) {
                    if(i * halfWidth + j < halfEnforcedOnes) {
                        fields[i][j] = Field(1)
                        countOne += 1
                        continue
                    }
                    
                    val currentSum = fields.sumOf { it -> it.sumOf { it.fish } }
                    val notFilled = totalSum - (i * halfWidth + j)
                    
                    val weightedSum = weightedInts.sumOf { it.second.toDouble() }
                    if(weightedSum.roundToInt() != 1) {
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
                    while(index < possibleValues.size && cumulativeWeight + possibleWeights[index] < value) {
                        cumulativeWeight += possibleWeights[index]
                        index++
                    }
                    
                    fields[i][j] = Field(possibleValues[index])
                    countOne += if(fields[i][j].fish == 1) 1 else 0
                }
            }
            
            for(i in 0 until length) {
                for(j in 0 until halfWidth) {
                    val x = random.nextInt(length)
                    val y = random.nextInt(halfWidth)
                    fields[i][j] = fields[x][y].also { fields[x][y] = fields[i][j] }
                }
            }
            
            for(i in 0 until length) {
                for(j in 0 until halfWidth) {
                    // TODO ??
                    //  fields[i] += fields[length - i - 1][halfWidth - j - 1]
                }
            }
            
            return fields.let {
                it + it.reversedArray().map { list ->
                    Array(Constants.BOARD_SIZE) { index -> list[Constants.BOARD_SIZE - index - 1].deepCopy() }
                }
            }
        }
    }
}
