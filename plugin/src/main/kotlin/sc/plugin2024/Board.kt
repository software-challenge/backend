package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sc.api.plugins.*
import java.util.Deque
import java.util.OptionalInt
import kotlin.math.abs

/**
 * Erzeugt ein neues Spielfeld anhand der gegebenen Segmente
 * @param segments Spielsegmente des neuen Spielfelds
 */
@XStreamAlias(value = "board")
data class Board(
        @XStreamOmitField
        private val segments: Segments = generateBoard(),
        @XStreamOmitField
        internal var visibleSegments: Int = 2,
): IBoard {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
    
    override fun clone(): Board = Board(this.segments.clone(), visibleSegments)
    
    // TODO direction of segment beyond visible one, set with visibleSegments
    @XStreamAsAttribute
    var nextDirection: CubeDirection? = null
    
    /**
     * Ruft das Feld an den angegebenen [CubeCoordinates] ab.
     *
     * @param coords Die [CubeCoordinates], die das abzurufende Feld angeben.
     * @return Das Feld an den angegebenen [CubeCoordinates], oder null, wenn kein Feld gefunden wird.
     */
    operator fun get(coords: CubeCoordinates) =
            segments.firstNotNullOfOrNull {
                val diff = coords - it.center
                logger.trace("{} - {}: {}", coords, it, diff)
                if(diff.distanceTo(CubeCoordinates.ORIGIN) <= 3)
                    it.segment[diff.rotatedBy(it.direction.turnCountTo(CubeDirection.RIGHT))]
                else
                    null
            }
    
    /**
     * Gibt den [FieldType] zurück, der an das angegebene Feld in der angegebenen Richtung angrenzt.
     *
     * @param direction die [HexDirection], in der das benachbarte Feld zu finden ist
     * @param coordinate die [Coordinates], für die das angrenzende Feld gefunden werden soll
     * @return das angrenzende [FieldType], wenn es existiert, sonst null
     */
    fun getFieldInDirection(direction: CubeDirection, coordinate: CubeCoordinates): FieldType? = get(coordinate + direction.vector)
    
    /**
     * Berechnet den Abstand zwischen zwei [FieldType]s in der Anzahl der [Segment].
     *
     * @param coordinate1 Das erste Feld, von dem aus die Entfernung berechnet wird.
     * @param coordinate2 Das zweite Feld, aus dem die Entfernung berechnet wird.
     * @return Der Abstand zwischen den angegebenen Feldern im Segment.
     * Wenn eines der Felder in keinem Segment gefunden wird, wird -1 zurückgegeben.
     */
    fun segmentDistance(coordinate1: CubeCoordinates, coordinate2: CubeCoordinates): Int {
        val segmentIndex1 = findSegment(coordinate1)
        val segmentIndex2 = findSegment(coordinate2)
        
        return if (segmentIndex1 != -1 && segmentIndex2 != -1) {
            abs(segmentIndex1 - segmentIndex2)
        } else {
            -1
        }
    }
    
    /**
     * Findet den [segments]-Index für die angegebene [CubeCoordinates].
     *
     * @param coordinate Die Koordinate, für die das [Segment] gefunden werden soll.
     * @return Der Index des Segments, das die Koordinate enthält, oder -1, falls nicht gefunden.
     */
    private fun findSegment(coordinate: CubeCoordinates): Int {
        segments.forEachIndexed { index, _ ->
            val fieldType = this[coordinate]
            if (fieldType != null) {
                return index
            }
        }
        return -1
    }

    /**
     * Gibt eine Liste benachbarter [FieldType]s auf der Grundlage der angegebenen [CubeCoordinates] zurück.
     *
     * @param coords die [CubeCoordinates] des Mittelfeldes
     * @return eine Liste der benachbarten [FieldType]s
     */
    fun neighboringFields(coords: CubeCoordinates): List<FieldType?> = CubeDirection.values().map { direction ->
        getFieldInDirection(direction, coords)
    }

    /**
     * Methode zur Abholung eines Passagiers auf einem [Ship].
     *
     * @param ship Das [Ship], mit dem der Passagier abgeholt wird.
     * @return `true`, wenn ein Passagier erfolgreich abgeholt wurde, sonst `false`.
     */
    fun pickupPassenger(ship: Ship): Boolean {
        val neighboringFields: List<FieldType?> = neighboringFields(ship.position)
        
        neighboringFields.forEach { field ->
            if(field is FieldType.PASSENGER && field.passenger > 0) {
                field.passenger--
                ship.passengers++
                
                return@pickupPassenger true
            }
        }
        
        return false
    }

    /**
     * Gibt die [CubeCoordinates] für einen bestimmten Index innerhalb eines Segments zurück.
     *
     * @param segmentIndex Der Index des Segments.
     * @param xIndex Der x-Index innerhalb des Segments.
     * @param yIndex Der y-Index innerhalb des Segments.
     * @return Die [CubeCoordinates] für den angegebenen Index innerhalb des Segments.
     */
    fun getCoordinateByIndex(segmentIndex: Int, xIndex: Int, yIndex: Int): CubeCoordinates {
        val segment = segments[segmentIndex]
        val diff = CubeCoordinates(xIndex, yIndex) + segment.center
        return diff.rotatedBy(-segment.direction.turnCountTo(CubeDirection.RIGHT))
    }

    /**
     * Findet das [Ship], das dem Ziel im letzten [Segment] am nächsten ist.
     *
     * @param ship1 Das erste [Ship] zum Vergleich.
     * @param ship2 Das zweite [Ship] zum Vergleich.
     * @return Das [Ship], das dem Ziel im letzten [Segment] am nächsten ist, oder null,
     * wenn beide Schiffe den gleichen Abstand haben.
     */
    fun closestShipToGoal(ship1: Ship, ship2: Ship): Ship? {
        val lastSegment = segments.last()
        val goalFields = lastSegment.segment.mapIndexed { i, array ->
            array.mapIndexed { j, fieldType ->
                if(fieldType == FieldType.GOAL) {
                    Pair(i, j)
                } else null
            }
        }.flatten().filterNotNull()

        var minDist1 = Int.MAX_VALUE
        var minDist2 = Int.MAX_VALUE

        for(pair in goalFields) {
            val coords = getCoordinateByIndex(segments.lastIndex, pair.first, pair.second)
            val dist1 = coords.distanceTo(ship1.position)
            val dist2 = coords.distanceTo(ship2.position)
            minDist1 = kotlin.math.min(minDist1, dist1)
            minDist2 = kotlin.math.min(minDist2, dist2)
        }

        return when {
            minDist1 < minDist2 -> ship1
            minDist1 > minDist2 -> ship2
            else -> null
        }
    }

    /**
     * Findet das nächstgelegene Feld des angegebenen [FieldType], ausgehend von den angegebenen [CubeCoordinates].
     *
     * @param startCoordinates Die Startkoordinaten.
     * @param fieldType Der FieldType, nach dem gesucht werden soll.
     * @return Die Koordinaten des nächstgelegenen Feldes des angegebenen Feldtyps oder null, wenn es nicht gefunden wurde.
     */
    fun findNearestFieldType(startCoordinates: CubeCoordinates, fieldType: FieldType): CubeCoordinates? {
        val visited = HashSet<CubeCoordinates>().apply { add(startCoordinates) }
        val queue: ArrayDeque<CubeCoordinates> = ArrayDeque<CubeCoordinates>().apply { add(startCoordinates) }
        
        while (queue.isNotEmpty()) {
            val currentCoordinates = queue.removeFirst()
            val currentField = this[currentCoordinates]
            
            if (currentField == fieldType) {
                return currentCoordinates
            }

            val neighbours = CubeDirection.values().map { direction -> currentCoordinates + direction.vector }

            for (neighbour in neighbours) {
                if (neighbour !in visited && this[neighbour] != null) {
                    visited.add(neighbour)
                    queue.add(neighbour)
                }
            }
        }
        
        return null
    }

    /**
     * Druckt die Segmente in einem lesbaren Format.
     *
     * Diese Methode durchläuft jedes Segment des gegebenen Objekts und druckt dessen Inhalt in formatierter Form aus.
     */
    fun prettyPrint() {
        val stringBuilder = StringBuilder()
        for ((index, segment) in this.segments.withIndex()) {
            stringBuilder.append("Segment ${index + 1}:\n")
            for (fieldTypeRow in segment.segment) {
                for (fieldType in fieldTypeRow) {
                    stringBuilder.append("| ${fieldType.javaClass.simpleName.firstOrNull() ?: '-'} ")
                }
                stringBuilder.append("|\n")
            }
            stringBuilder.append("\n")
        }
        print(stringBuilder.toString())
    }
    
}

