package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.converters.collections.ArrayConverter
import com.thoughtworks.xstream.converters.extended.ToStringConverter
import sc.api.plugins.IBoard
import sc.plugin2020.util.Constants
import sc.plugin2020.util.CubeCoordinates
import sc.api.plugins.ITeam
import kotlin.collections.HashSet
import kotlin.math.max
import kotlin.math.min

@XStreamAlias(value = "board")
data class Board(
        @XStreamConverter(value = ArrayConverter::class, nulls = [ToStringConverter::class])
        @XStreamImplicit(itemFieldName = "fields")
        val gameField: Array<Array<Field?>>
): IBoard {
    
    val fields: List<Field>
        get() = gameField.flatMap { it.filterNotNull() }
    
    constructor(): this(fillGameField()) {
        blockFields(randomFields())
    }
    
    /** Creates a new Board with blockers at the given [blockedFields]. */
    constructor(vararg blockedFields: CubeCoordinates): this() {
        blockFields(blockedFields.toSet())
    }
    
    /** Creates a new Board from clones of the given [fields]. Does not fill it up! */
    constructor(fields: Collection<Field>): this(gameFieldFromFields(fields))
   
    /** Copy constructor to create a new deeply copied state from the given [board]. */
    constructor(board: Board): this(board.fields)
   
    /** Creates a deep copy of this [Board]. */
    public override fun clone() = Board(this)
    
    /** Returns [amount] random fields from [fields]. */
    fun randomFields(amount: Int = 3): Collection<Field> {
        val all = this.fields
        val fields = HashSet<Field>()
        while(fields.size < amount)
            fields.add(all.random())
        return fields
    }
    
    private fun blockFields(fields: Collection<CubeCoordinates>) {
        fields.forEach {
            this.gameField[it.x + SHIFT][it.y + SHIFT] = Field(it.x, it.y, true)
        }
    }
    
    fun getField(pos: CubeCoordinates): Field =
            gameField[pos.x + SHIFT][pos.y + SHIFT] ?: throw IndexOutOfBoundsException("No field at $pos")
    
    override fun getField(cubeX: Int, cubeY: Int): Field =
            this.getField(CubeCoordinates(cubeX, cubeY))
    
    fun getField(cubeX: Int, cubeY: Int, cubeZ: Int): Field =
            this.getField(CubeCoordinates(cubeX, cubeY, cubeZ))
    
    /** @return all Pieces on the Board. Prefer [GameState.getDeployedPieces] if possible for better performance. */
    fun getPieces(): List<Piece> {
        val pieces = mutableListOf<Piece>()
        for(x in -SHIFT..SHIFT) {
            for(y in max(-SHIFT, -x - SHIFT)..min(SHIFT, -x + SHIFT)) {
                val field = gameField[x + SHIFT][y + SHIFT]
                if(field != null)
                    pieces.addAll(field.pieces)
            }
        }
        return pieces
    }
    
    override fun toString(): String {
        val text = StringBuilder("Board")
        for (x in 0 until Constants.BOARD_SIZE) {
            val line = StringBuilder()
            for (y in 0 until Constants.BOARD_SIZE) {
                val field = this.gameField[x][y]
                when {
                    field == null -> line.insert(0, ' ')
                    !field.hasOwner -> line.append("[]")
                    else -> line.append(field.owner!!.letter).append(field.topPiece!!.type.letter)
                }
            }
            text.append('\n').append(line)
        }
        return text.toString()
    }
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        return gameField.contentDeepEquals((other as Board).gameField)
    }
    
    override fun hashCode(): Int =
            gameField.contentDeepHashCode()
    
    fun getFieldsOwnedBy(owner: ITeam): List<Field> = fields.filter { it.owner == owner }
    
    companion object {
        private const val SHIFT = (Constants.BOARD_SIZE - 1) / 2
        
        private fun emptyGameField() = Array(Constants.BOARD_SIZE) { arrayOfNulls<Field>(Constants.BOARD_SIZE) }
        
        /** Fills the given [gameField] with all missing Fields for a valid Board. */
        @JvmStatic
        fun fillGameField(gameField: Array<Array<Field?>> = emptyGameField()): Array<Array<Field?>> {
            for(x in -SHIFT..SHIFT) {
                for(y in max(-SHIFT, -x - SHIFT)..min(SHIFT, -x + SHIFT)) {
                    if(gameField[x + SHIFT][y + SHIFT] == null) {
                        gameField[x + SHIFT][y + SHIFT] = Field(CubeCoordinates(x, y))
                    }
                }
            }
            return gameField
        }
        
        /** Creates a new gameField from clones of the given [fields].
         * @return a gameField consisting only of clones of the [fields] */
        @JvmStatic
        fun gameFieldFromFields(fields: Collection<Field>): Array<Array<Field?>> {
            val gameField = emptyGameField()
            var x: Int
            var y: Int
            for(f in fields) {
                if(f.coordinates.x > SHIFT || f.coordinates.x < -SHIFT || f.coordinates.y > SHIFT || f.coordinates.y < -SHIFT)
                    throw IndexOutOfBoundsException()
                x = f.coordinates.x + SHIFT
                y = f.coordinates.y + SHIFT
                gameField[x][y] = f.clone()
            }
            return gameField
        }
    }
}