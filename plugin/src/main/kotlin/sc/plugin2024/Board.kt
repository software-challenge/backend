package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sc.api.plugins.*
import sc.plugin2024.util.PluginConstants.POINTS_PER_PASSENGER
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 * Erzeugt ein neues Spielfeld anhand der gegebenen Segmente
 * @param segments Spielsegmente des neuen Spielfelds
 */
@XStreamAlias(value = "board")
data class Board(
        @XStreamImplicit
        val segments: Segments = generateBoard(),
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
                if(coords.distanceTo(it.center) <= 3)
                    it[coords]
                else
                    null
            }
    
    /**
     * Gibt das [Field] zurück, das an das angegebene Feld in der angegebenen Richtung angrenzt.
     *
     * @param direction die [HexDirection], in der das benachbarte Feld zu finden ist
     * @param coordinate die [Coordinates], für die das angrenzende Feld gefunden werden soll
     *
     * @return das angrenzende [Field], wenn es existiert, sonst null
     */
    fun getFieldInDirection(direction: CubeDirection, coordinate: CubeCoordinates): Field? =
            get(coordinate + direction.vector)
    
    /**
     * Gibt die [CubeCoordinates] für einen bestimmten Index innerhalb eines Segments zurück.
     *
     * @param segmentIndex Der Index des Segments.
     * @param xIndex Der x-Index innerhalb des Segments.
     * @param yIndex Der y-Index innerhalb des Segments.
     * @return Die [CubeCoordinates] für den angegebenen Index innerhalb des Segments.
     */
    fun getCoordinateByIndex(segmentIndex: Int, xIndex: Int, yIndex: Int): CubeCoordinates =
            segments[segmentIndex].let { segment ->
                val r = yIndex - 2
                CubeCoordinates(xIndex - 1 - r.coerceAtLeast(0), r)
                        .rotatedBy(CubeDirection.RIGHT.turnCountTo(segment.direction))
                        .plus(segment.center)
            }
    
    /**
     * Berechnet den Abstand zwischen zwei [Field]s in der Anzahl der [Segment].
     *
     * @param coordinate1 Das erste Feld, von dem aus die Entfernung berechnet wird.
     * @param coordinate2 Das zweite Feld, aus dem die Entfernung berechnet wird.
     * @return Der Abstand zwischen den angegebenen Feldern im Segment.
     * Wenn eines der Felder in keinem Segment gefunden wird, wird -1 zurückgegeben.
     */
    fun segmentDistance(coordinate1: CubeCoordinates, coordinate2: CubeCoordinates): Int? =
            findSegment(coordinate1)?.let { index1 ->
                findSegment(coordinate2)?.let { index2 ->
                    abs(index1 - index2)
                }
            }
    
    /**
     * Findet den [segments]-Index für die angegebene [CubeCoordinates].
     *
     * @param coordinate Die Koordinate, für die das [Segment] gefunden werden soll.
     * @return Der Index des Segments, das die Koordinate enthält, oder -1, falls nicht gefunden.
     */
    fun findSegment(coordinate: CubeCoordinates): Int? =
            segments.indexOfFirst { segment ->
                segment[coordinate] != null
            }.takeUnless { it == -1 }
    
    /**
     * Gibt eine Liste benachbarter [Field]s auf der Grundlage der angegebenen [CubeCoordinates] zurück.
     *
     * @param coords die [CubeCoordinates] des Mittelfeldes
     * @return eine Liste der benachbarten [Field]s
     */
    fun neighboringFields(coords: CubeCoordinates): List<Field?> =
            CubeDirection.values().map { direction ->
                getFieldInDirection(direction, coords)
            }
    
    /**
     * Methode zur Abholung eines Passagiers auf einem [Ship].
     *
     * @param ship Das [Ship], mit dem der Passagier abgeholt wird.
     * @return `true`, wenn ein Passagier erfolgreich abgeholt wurde, sonst `false`.
     */
    fun pickupPassenger(ship: Ship): Boolean =
            neighboringFields(ship.position)
                    .filterIsInstance<Field.PASSENGER>().firstOrNull { it.passenger > 0 }?.run {
                        passenger--
                        ship.passengers++
                        ship.points += POINTS_PER_PASSENGER
                        true
                    } ?: false
    
    /**
     * Findet das [Ship], das dem Ziel im letzten [Segment] am nächsten ist.
     *
     * @param ship1 Das erste [Ship] zum Vergleich.
     * @param ship2 Das zweite [Ship] zum Vergleich.
     * @return Das [Ship], das dem Ziel im letzten [Segment] am nächsten ist, oder null,
     * wenn beide Schiffe den gleichen Abstand haben.
     */
    fun closestShipToGoal(ship1: Ship, ship2: Ship): Ship? =
            with(segments.last()) {
                // TODO this assumes that the segment is 5x4
                val mostFurthestPoint = tip
                val dist1 = ship1.position.distanceTo(mostFurthestPoint)
                val dist2 = ship2.position.distanceTo(mostFurthestPoint)
                if(dist1 < dist2) ship1 else if(dist1 > dist2) ship2 else null
            }
    
    /**
     * Findet das nächstgelegene Feld des angegebenen [Field], ausgehend von den angegebenen [CubeCoordinates],
     * aber ohne [startCoordinates].
     *
     * @param startCoordinates Die Startkoordinaten.
     * @param field Der [Field], nach dem gesucht werden soll.
     * @return Eine Liste von [CubeCoordinates], die die nächstgelegenen Feldkoordinaten mit dem angegebenen [Field] darstellen.
     */
    fun findNearestFieldTypes(startCoordinates: CubeCoordinates, field: KClass<out Field>): List<CubeCoordinates> {
        val visitedCoordinates = mutableSetOf<CubeCoordinates>()
        val neighbourCoordinatesQueue = ArrayDeque<CubeCoordinates>()
        val nearestFieldCoordinates: MutableList<CubeCoordinates> = mutableListOf()
        
        /** Prüft das [Field] an den angegebenen [CubeCoordinates] und fügt es ggf. der [ArrayDeque] hinzu. */
        fun checkFieldAndAddToQueue(coordinates: CubeCoordinates) {
            val neighbourField = this[coordinates]
            if(neighbourField != null && !visitedCoordinates.contains(coordinates) && !neighbourCoordinatesQueue.contains(coordinates)) {
                neighbourCoordinatesQueue.add(coordinates)
            }
        }
        
        CubeDirection.values().forEach { direction ->
            checkFieldAndAddToQueue(startCoordinates + direction.vector)
        }
        
        while(neighbourCoordinatesQueue.isNotEmpty()) {
            val currentCoordinates = neighbourCoordinatesQueue.removeFirst()
            
            if(nearestFieldCoordinates.isNotEmpty() && startCoordinates.distanceTo(currentCoordinates) > startCoordinates.distanceTo(nearestFieldCoordinates.last()))
                break
            
            visitedCoordinates.add(currentCoordinates)
            
            val currentField = this[currentCoordinates]
            if(currentField != null && field.isInstance(currentField)) {
                nearestFieldCoordinates.add(currentCoordinates)
            } else {
                CubeDirection.values().forEach { direction ->
                    checkFieldAndAddToQueue(currentCoordinates + direction.vector)
                }
            }
        }
        
        return nearestFieldCoordinates
    }
    
    /**
     * Druckt die Segmente in einem lesbaren Format.
     *
     * Diese Methode durchläuft jedes Segment des gegebenen Objekts und druckt dessen Inhalt in formatierter Form aus.
     */
    fun prettyPrint() {
        /*segments.joinToString("\n\n") {
            "Segment pointing ${it.direction}" +
            // TODO how to join columns
        }*/
        val stringBuilder = StringBuilder()
        for((segmentIndex, segment) in this.segments.withIndex()) {
            stringBuilder.append("Segment ${segmentIndex + 1}:\n")
            for((x, column) in segment.segment.withIndex()) {
                for((y, field) in column.withIndex()) {
                    val cubeCoordinates = getCoordinateByIndex(segmentIndex, x, y)
                    stringBuilder.append("| ${field.letter} (${cubeCoordinates.q}, ${cubeCoordinates.r}) ")
                }
                stringBuilder.append("|\n")
            }
            stringBuilder.append("\n")
        }
        print(stringBuilder.toString())
    }
    
}

