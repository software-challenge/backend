package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamConverter
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.converters.collections.CollectionConverter
import sc.framework.PublicCloneable

typealias TwoDBoard<FIELD> = List<MutableList<FIELD>>

fun <T: Cloneable> List<List<PublicCloneable<T>>>.clone() =
        List(size) { row -> MutableList(this[row].size) { column -> this[row][column].clone() } }

/** Ein rechteckiges Spielfeld aus Feldern, die jeweils von einer Spielerfarbe belegt sein können. */
open class RectangularBoard<FIELD: IField<FIELD>>(
        @XStreamImplicit protected val gameField: TwoDBoard<FIELD> = emptyList(),
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
    
    open fun isValid(coordinates: Coordinates) =
            coordinates.y in gameField.indices &&
            coordinates.x in gameField[coordinates.y].indices
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück.
     * Bevorzugt für interne Verwendung, da Fehler roh zurückgegeben werden. */
    open operator fun get(x: Int, y: Int) =
            gameField[y][x]
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    @Throws(IllegalArgumentException::class)
    override operator fun get(key: Coordinates) =
            try {
                get(key.x, key.y)
            } catch(e: IndexOutOfBoundsException) {
                outOfBounds(key, e)
            }
    
    protected fun outOfBounds(coords: Coordinates, cause: Throwable? = null): Nothing =
            throw IllegalArgumentException("$coords ist nicht teil des Spielfelds!", cause)
    
    fun getOrNull(key: Coordinates) =
            if(isValid(key))
                get(key.x, key.y)
            else
                null
    
    /** Vergleicht zwei Spielfelder und gibt eine Liste aller Felder zurück, die sich unterscheiden. */
    fun compare(other: RectangularBoard<FIELD>): Collection<FIELD> {
        val entries = this.entries
        return other.entries.filter {
            it !in entries
        }.map { it.value }
    }
    
    override fun toString() =
            gameField.joinToString(separator = "\n") { row ->
                row.joinToString(separator = "") { it.toString() }
            }.ifEmpty { "Empty Board@" + System.identityHashCode(this) }
    
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
    
    override fun iterator(): Iterator<FIELD> = object: AbstractIterator<FIELD>() {
        var index = 0
        override fun computeNext() {
            if(index < size)
                setNext(get(index))
            else
                done()
            index++
        }
    }
    
    override fun containsAll(elements: Collection<FIELD>): Boolean = elements.all { contains(it) }
    
    override fun contains(element: FIELD): Boolean = gameField.any { it.contains(element) }
    
    // TODO do this properly for non-squared boards
    operator fun get(index: Int): FIELD =
            gameField[index.div(gameField.size)][index.mod(gameField.size)]
    
    fun readResolve(): Any {
        @Suppress("SENSELESS_COMPARISON")
        if(gameField == null) {
            val field = RectangularBoard::class.java.getDeclaredField("gameField")
            field.isAccessible = true
            field.set(this, ArrayList<MutableList<FIELD>>())
        }
        return this
    }
    
    override fun equals(other: Any?) = gameField == (other as? RectangularBoard<*>)?.gameField
    
    override fun hashCode(): Int = gameField.hashCode()
}
