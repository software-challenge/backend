package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.IBoard

@XStreamAlias("board")
class Board(
    @XStreamImplicit(itemFieldName = "field")
    private val track: Array<out Field> = generateTrack().toTypedArray(),
): IBoard {
    
    val size
        get() = track.size
    
    val fields
        get() = track.iterator()
    
    fun getField(index: Int): Field? =
        track.getOrNull(index)
    
    /** Findet die Position eines Feldes dieser Art im angegebenen Bereich. */
    fun findField(field: Field, range: Iterable<Int> = 0 until size): Int? =
        range.find { track[it] == field }
    
    /** Findet die Position des vorherigen Feldes dieser Art vor dem angegebenen Feldindex. */
    fun getPreviousField(field: Field, index: Int = size) =
        findField(field, (index - 1) downTo (0))
    
    /** Findet die Position des nächsten Feldes dieser Art nach dem angegebenen Feldindex. */
    fun getNextField(field: Field, index: Int = 0) =
        findField(field, (index + 1).until(size))
    
    override fun toString() =
        track.joinToString(prefix = "Board[", postfix = "]") { it.unicode }
    
    override fun clone(): Board = this
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Board) return false
        return track.contentEquals(other.track)
    }
    
    override fun hashCode(): Int =
        track.contentHashCode()
    
    companion object {
        
        private fun shuffledFields(vararg fields: Field) = fields.asList().shuffled()
        
        /**
         * Erstellt eine zufällige Rennstrecke.
         * Die Indizes der Salat- und Igelfelder bleiben unverändert -
         * nur die Felder zwischen zwei Igelfeldern werden permutiert.
         * Außerdem werden auch die Abschnitte zwischen Start- und Ziel
         * und dem ersten bzw. letzten Igelfeld permutiert.
         */
        private fun generateTrack(): List<Field> {
            val track = ArrayList<Field>()
            
            track.add(Field.START)
            track.addAll(
                shuffledFields(
                    Field.HARE,
                    Field.CARROTS,
                    Field.HARE,
                    Field.POSITION_3,
                    Field.CARROTS,
                    Field.HARE,
                    Field.POSITION_1,
                    Field.POSITION_2,
                    Field.POSITION_4
                )
            )
            
            track.add(Field.SALAD)
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.POSITION_3,
                    Field.CARROTS,
                    Field.HARE
                )
            )
            
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.POSITION_1,
                    Field.POSITION_2,
                    Field.POSITION_4
                )
            )
            
            track.add(Field.HEDGEHOG)
            val fields = shuffledFields(
                Field.POSITION_3,
                Field.CARROTS,
                Field.POSITION_2,
            )
            track.addAll(fields.take(2))
            track.add(Field.SALAD)
            track.add(fields.last())
            
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.HARE,
                    Field.CARROTS,
                    Field.POSITION_4,
                    Field.POSITION_3,
                    Field.POSITION_2
                )
            )
            
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.HARE,
                    Field.POSITION_1,
                    Field.CARROTS,
                    Field.HARE,
                    Field.POSITION_2,
                    Field.POSITION_3
                )
            )
            
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.CARROTS,
                    Field.HARE,
                    Field.CARROTS,
                    Field.POSITION_2
                )
            )
            
            track.add(Field.SALAD)
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.POSITION_3,
                    Field.POSITION_4,
                    Field.HARE,
                    Field.POSITION_2,
                    Field.POSITION_1,
                    Field.CARROTS
                )
            )
            
            track.add(Field.HEDGEHOG)
            track.addAll(
                shuffledFields(
                    Field.HARE,
                    Field.POSITION_3,
                    Field.POSITION_2,
                    Field.POSITION_4,
                    Field.CARROTS
                )
            )
            
            track.add(Field.HEDGEHOG)
            track.add(Field.SALAD)
            
            track.addAll(
                listOf(
                    Field.HARE,
                    Field.CARROTS,
                    Field.POSITION_1,
                    Field.CARROTS,
                    Field.HARE,
                    Field.CARROTS
                )
            )
            
            track.add(Field.GOAL)
            return track
        }
    }
}