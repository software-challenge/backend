package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.IBoard
import sc.plugin2025.util.HuIConstants

@XStreamAlias("board")
data class Board(
    @XStreamImplicit(itemFieldName = "fields") private val track: Array<Field> = generateTrack().toTypedArray(),
): IBoard {
    fun findField(field: Field, range: Iterable<Int> = 0 until HuIConstants.NUM_FIELDS) =
        range.find { track[it] == field }
    
    fun getField(index: Int) = track[index]
    
    fun getPreviousField(field: Field, index: Int = 0) =
        findField(field, (index - 1) downTo (0))
    
    fun getNextField(field: Field, index: Int = 0) =
        findField(field, (index + 1).until(HuIConstants.NUM_FIELDS))
    
    override fun toString() =
        track.joinToString(prefix = "Board[", postfix = "]") { it.unicode }
    
    override fun clone(): Board = Board(Array(track.size) { track[it] })
    
    companion object {
        /**
         * Erstellt eine zufällige Rennstrecke.
         * Die Indizes der Salat- und Igelfelder bleiben unverändert -
         * nur die Felder zwischen zwei Igelfeldern werden permutiert.
         * Außerdem werden auch die Abschnitte zwischen Start- und Ziel
         * und dem ersten bzw. letzten Igelfeld permutiert.
         */
        private fun generateTrack(): List<Field> {
            val track = ArrayList<Field>()
            val segment = ArrayList<Field>()
            
            track.add(Field.START)
            segment.addAll(
                listOf(
                    Field.HARE,
                    Field.CARROT, Field.HARE, Field.CARROT,
                    Field.CARROT, Field.HARE, Field.POSITION_1,
                    Field.POSITION_2, Field.CARROT
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.SALAD)
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.CARROT,
                    Field.CARROT, Field.HARE
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.POSITION_1,
                    Field.POSITION_2, Field.CARROT
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.CARROT,
                    Field.CARROT, Field.POSITION_2
                )
            )
            segment.shuffle()
            
            track.add(segment.removeAt(0))
            track.add(segment.removeAt(0))
            track.add(Field.SALAD)
            track.add(segment.removeAt(0))
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.HARE,
                    Field.CARROT, Field.CARROT, Field.CARROT,
                    Field.POSITION_2
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.HARE,
                    Field.POSITION_1, Field.CARROT, Field.HARE,
                    Field.POSITION_2, Field.CARROT
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.CARROT,
                    Field.HARE, Field.CARROT, Field.POSITION_2
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.SALAD)
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.CARROT,
                    Field.CARROT, Field.HARE, Field.POSITION_2,
                    Field.POSITION_1, Field.CARROT
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.HEDGEHOG)
            segment.addAll(
                listOf(
                    Field.HARE,
                    Field.CARROT, Field.POSITION_2, Field.CARROT,
                    Field.CARROT
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            track.add(Field.HEDGEHOG)
            track.add(Field.SALAD)
            
            segment.clear()
            segment.addAll(
                listOf(
                    Field.HARE,
                    Field.CARROT, Field.POSITION_1, Field.CARROT,
                    Field.HARE, Field.CARROT
                )
            )
            segment.shuffle()
            track.addAll(segment)
            
            segment.clear()
            track.add(Field.GOAL)
            return track
        }
    }
}