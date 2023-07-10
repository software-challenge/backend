package sc.api.plugins

import sc.framework.PublicCloneable

interface IBoard: PublicCloneable<IBoard>

abstract class FieldMap<FIELD>: AbstractMap<Coordinates, FIELD>() {
    /** Gibt das Feld an den gegebenen Koordinaten zurück.
     * Bevorzugt für interne Verwendung, da Fehler ungefiltert zurückgegeben werden. */
    abstract operator fun get(x: Int, y: Int): FIELD

    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    @Throws(IllegalArgumentException::class)
    override operator fun get(key: Coordinates): FIELD =
            try {
                get(key.x, key.y)
            } catch(e: IndexOutOfBoundsException) {
                outOfBounds(key, e)
            }
    
    fun outOfBounds(coords: Coordinates, cause: Throwable? = null): Nothing =
            throw IllegalArgumentException("$coords ist nicht teil des Spielfelds!", cause)
    
    
    inner class FieldPosition(
            override val key: Coordinates,
            override val value: FIELD
    ): Map.Entry<Coordinates, FIELD>
    
}