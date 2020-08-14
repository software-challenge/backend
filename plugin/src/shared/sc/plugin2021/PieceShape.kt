package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.plugin2021.util.*
import kotlin.math.max

@XStreamAlias(value = "shape")
enum class PieceShape(coordinates: Set<Coordinates>) {
/* 0*/  MONO   (setOf(Coordinates(0, 0))),
/* 1*/  DOMINO (setOf(Coordinates(0, 0), Coordinates(1, 0))),
/* 2*/  TRIO_L (setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1))),
/* 3*/  TRIO_I (setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2))),
/* 4*/  TETRO_O(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1))),
/* 5*/  TETRO_T(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(1, 1))),
/* 6*/  TETRO_I(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(0, 3))),
/* 7*/  TETRO_L(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2))),
/* 8*/  TETRO_Z(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(2, 1))),
/* 9*/  PENTO_L(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(0, 3), Coordinates(1, 3))),
/*10*/  PENTO_T(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(1, 1), Coordinates(1, 2))),
/*11*/  PENTO_V(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2))),
/*12*/  PENTO_S(setOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(3, 0), Coordinates(0, 1), Coordinates(1, 1))),
/*13*/  PENTO_Z(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 2))),
/*14*/  PENTO_I(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(0, 3), Coordinates(0, 4))),
/*15*/  PENTO_P(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(0, 2))),
/*16*/  PENTO_W(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 2))),
/*17*/  PENTO_U(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0))),
/*18*/  PENTO_R(setOf(Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 1), Coordinates(2, 0))),
/*19*/  PENTO_X(setOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(1, 2))),
/*20*/  PENTO_Y(setOf(Coordinates(0, 1), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(1, 3)));
    
    
    @XStreamAsAttribute
    val coordinates: Set<Coordinates> = coordinates.align()
    
    @XStreamAsAttribute
    val dimension: Vector = coordinates.area()
    
    val asVectors: Set<Vector> by lazy {coordinates.map {it - Coordinates.origin}.toSet()}
    
    @XStreamOmitField
    val size: Int = coordinates.size
    
    /** All different variants. Boiler plate for faster calculation of possible moves. */
    val variants: Map<Set<Coordinates>, Pair<Rotation, Boolean>>
    
    val transformations: Map<Pair<Rotation, Boolean>, Set<Coordinates>>
    
    init {
        val mapVariants = mutableMapOf<Set<Coordinates>, Pair<Rotation, Boolean>>()
        val mapTransformations = mutableMapOf<Pair<Rotation, Boolean>, Set<Coordinates>>()
        for (rotation in Rotation.values()) {
            for (flip in listOf(false, true)) {
                val shape = coordinates.rotate(rotation).flip(flip)
                if (mapVariants[shape] == null) mapVariants += shape to Pair(rotation, flip)
                mapTransformations += Pair(rotation, flip) to shape
            }
        }
        variants = mapVariants
        transformations = mapTransformations
    }
    
    /** Does the same thing as transform, providing the index operator. */
    operator fun get(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            transform(rotation, shouldFlip)
    
    /** Returns a shape with all transformations applied. */
    fun transform(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            transformations[rotation to shouldFlip] ?: emptySet()
    
    /** Applies all the given transformations manually instead of looking them up. */
    fun legacyTransform(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            coordinates.rotate(rotation).flip(shouldFlip)
    
    companion object {
        val shapes: Map<Int, PieceShape> = ((0 until Constants.TOTAL_PIECE_SHAPES) zip values()).toMap()
    }
}
