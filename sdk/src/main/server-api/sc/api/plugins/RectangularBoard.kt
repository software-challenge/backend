package sc.api.plugins

import sc.framework.PublicCloneable

typealias TwoDBoard<FIELD> = List<MutableList<FIELD>>

fun <T: Cloneable> List<List<PublicCloneable<T>>>.clone() =
        List(size) { row -> MutableList(this[row].size) { column -> this[row][column].clone() } }

/** Ein rechteckiges Spielfeld aus Feldern, die jeweils von einer Spielerfarbe belegt sein können. */
open class RectangularBoard<FIELD: IField<FIELD>>(
        protected val gameField: TwoDBoard<FIELD>,
): IBoard, AbstractMap<Coordinates, FIELD>(), Collection<FIELD> {
    
    constructor(other: RectangularBoard<FIELD>): this(other.gameField.clone())
    
    /** Prüft, ob alle Felder leer sind. */
    fun fieldsEmpty() =
            gameField.all { row ->
                row.all { it.isEmpty }
            }
    
    /** Prüft, ob auf dieser [position] bereits eine Spielfigur ist. */
    fun isObstructed(position: Coordinates): Boolean =
            this[position].isOccupied
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    open operator fun get(x: Int, y: Int) =
            gameField[y][x]
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    override operator fun get(key: Coordinates) =
            get(key.x, key.y)
    
    
    /** Vergleicht zwei Spielfelder und gibt eine Liste aller Felder zurück, die sich unterscheiden. */
    //fun compare(other: Board): Set<Field> {
    //    val changedFields = mutableSetOf<Field>()
    //    for (y in 0 until Constants.BOARD_SIZE) {
    //        for (x in 0 until Constants.BOARD_SIZE) {
    //            if (gameField[y][x] != other.gameField[y][x]) {
    //                changedFields += Field(Coordinates(x, y), other.gameField[y][x])
    //            }
    //        }
    //    }
    //    return changedFields
    //}
    
    override fun toString() =
            gameField.joinToString(separator = "\n") { row ->
                row.joinToString(separator = " ") { it.toString() }
            }
    
    override fun clone() = RectangularBoard(this.gameField)
    
    override val entries: Set<Map.Entry<Coordinates, FIELD>>
        get() = gameField.flatMapIndexed { y, row ->
            row.mapIndexed { x, field ->
                // TODO really? an anonymous object?
                object: Map.Entry<Coordinates, FIELD> {
                    override val key = Coordinates(x, y)
                    override val value = field
                }
            }
        }.toSet()
    
    override val size: Int
        get() = gameField.sumOf { it.size }
    
    override fun iterator(): Iterator<FIELD> = object : AbstractIterator<FIELD>() {
        var index = 0
        override fun computeNext() {
            if(index < size)
                setNext(get(index))
            else
                done()
            index++
        }
    }
    
    override fun containsAll(elements: Collection<FIELD>): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun contains(element: FIELD): Boolean {
        TODO("Not yet implemented")
    }
    
    // TODO do this properly for non-squared boards
    operator fun get(index: Int): FIELD =
            gameField[index.div(gameField.size)][index.mod(gameField.size)]
}
