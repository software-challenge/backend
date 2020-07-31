package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.plugin2021.util.Constants
import sc.plugin2021.util.align
import sc.plugin2021.util.rotate
import sc.plugin2021.util.flip
import kotlin.math.max

enum class PieceShape(coordinates: Set<Coordinates>) {
    MONO   (setOf(Coordinates(0, 0))),
    DOMINO (setOf(Coordinates(0, 0), Coordinates(1, 0))),
    TRIO_L (setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1))),
    TRIO_I (setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0))),
    TETRO_O(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1))),
    TETRO_Z(setOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1))),
    TETRO_I(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(3, 0))),
    TETRO_J(setOf(Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1))),
    TETRO_T(setOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1))),
    PENTO_Q(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(3, 1))),
    PENTO_T(setOf(Coordinates(1, 0), Coordinates(1, 1), Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2))),
    PENTO_V(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2))),
    PENTO_S(setOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(3, 0), Coordinates(0, 1), Coordinates(1, 1))),
    PENTO_Z(setOf(Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(0, 2))),
    PENTO_O(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(0, 3), Coordinates(0, 4))),
    PENTO_P(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(0, 2), Coordinates(1, 2))),
    PENTO_W(setOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(0, 2))),
    PENTO_U(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2))),
    PENTO_R(setOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2))),
    PENTO_X(setOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(1, 2))),
    PENTO_Y(setOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(3, 1)));
    
    
    @XStreamAsAttribute
    val coordinates: Set<Coordinates> = coordinates.align()
    @XStreamAsAttribute
    val dimension: Vector
    
    val asVectors: Set<Vector> by lazy {coordinates.map {it - Coordinates.origin}.toSet()}
    @XStreamOmitField
    val size: Int = coordinates.size
    
    init {
        var dx = 0
        var dy = 0
        coordinates.forEach {
            dx = max(it.x, dx)
            dy = max(it.y, dy)
        }
        dimension = Vector(dx, dy)
    }
    
    /** Applies all the given transformations. */
    fun transform(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            coordinates.flip(shouldFlip).rotate(rotation)
    
    companion object {
        val shapes: Map<Int, PieceShape> = ((0 until Constants.TOTAL_PIECE_SHAPES) zip values()).toMap()
    }
}
