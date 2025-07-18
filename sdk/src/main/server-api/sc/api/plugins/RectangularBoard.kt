package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamImplicit

/** Eine zweidimensionale Anordnung von Feldern, eine Liste von Zeilen. */
typealias TwoDBoard<FIELD> = Array<Array<FIELD>>

/** Eine zweidimensionale Anordnung von Feldern, eine Liste von Zeilen,
 * wo Elemente verändert werden können. */
typealias MutableTwoDBoard<FIELD> = Array<Array<FIELD>>

/**
 * Ein rechteckiges Spielfeld aus Feldern.
 * Intern repräsentiert durch eine Liste an Zeilen.
 */
open class RectangularBoard<FIELD: IField>(
        @XStreamImplicit protected open val gameField: TwoDBoard<FIELD>
): FieldMap<FIELD>() {
    
    override val size: Int
        get() = gameField.size * columnCount
            // For non-rectangular: gameField.sumOf { it.size }
    
    /** Anzahl der Spalten im Spielfeld.
     * Geht von der ersten Zeile aus, da das Spielfeld ja rechteckig ist. */
    val columnCount: Int
        get() = gameField.first().size
    
    /** Prüft, ob alle Felder leer sind. */
    fun fieldsEmpty() =
            gameField.all { row ->
                row.all { it.isEmpty }
            }
    
    /** Sind die [coordinates] teil des (rechteckigen) Spielfelds? */
    open fun isValid(coordinates: Coordinates) =
            coordinates.y in gameField.indices &&
            coordinates.x in gameField[coordinates.y].indices
    
    override operator fun get(x: Int, y: Int): FIELD =
            gameField[y][x]
    
    operator fun set(coordinates: Coordinates, field: FIELD) {
        gameField[coordinates.y][coordinates.x] = field
    }
    
    operator fun set(x: Int, y: Int, field: FIELD) {
        gameField[y][x] = field
    }
    
    /** Gibt ein Feld zurück wenn die Koordinaten valide sind. */
    fun getOrNull(key: Coordinates) =
            if(isValid(key))
                get(key.x, key.y)
            else
                null
    
    /** Vergleicht zwei Spielfelder und gibt eine Liste aller Felder zurück, die sich unterscheiden. */
    fun compare(other: RectangularBoard<FIELD>): Collection<FIELD> {
        val entries = this.entries
        return other.entries
            .filter { it !in entries }
            .map { it.value }
    }
    
    override fun toString() =
            gameField.joinToString(separator = "\n") { row ->
                row.joinToString(separator = "") { it.toString() }
            }.ifEmpty { "Empty Board@" + System.identityHashCode(this) }
    
    override val entries: Set<Positioned<FIELD>>
        get() = gameField.flatMapIndexedTo(HashSet()) { y, row ->
            row.mapIndexed { x, field ->
                Positioned(Coordinates(x, y), field)
            }
        }
    
    fun iterateFields(): Iterator<FIELD> = object: AbstractIterator<FIELD>() {
        var index = 0
        override fun computeNext() {
            if(index < size)
                setNext(get(index))
            else
                done()
            index++
        }
    }
    
    /** Get an element row by row. */
    protected operator fun get(index: Int): FIELD =
        gameField[index.div(columnCount)][index.mod(columnCount)]
    
    /** Initializes an empty gameField when created by XStream. */
    fun readResolve(): Any {
        @Suppress("SENSELESS_COMPARISON")
        if(gameField == null) {
            val field = RectangularBoard::class.java.getDeclaredField("gameField")
            field.isAccessible = true
            field.set(this, ArrayList<MutableList<FIELD>>())
        }
        return this
    }
    
    override fun equals(other: Any?) =
            other is RectangularBoard<*> && gameField.contentDeepEquals(other.gameField)
    
    override fun hashCode(): Int = gameField.contentDeepHashCode()
}