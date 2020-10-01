package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.plugin2021.util.*
import kotlin.math.max

/** Eine Enumeration aller 21 verschiedenen Formen, als Set of [Coordinates]. */
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
    
    /** Die normalisierten Koordinaten, die die tatsächliche Form ausmachen. */
    @XStreamAsAttribute
    val coordinates: Set<Coordinates> = coordinates.align()
    
    /** Ein Vector, der das kleinstmögliche Rechteck beschreibt, dass die vollständige Form umfasst. */
    @XStreamAsAttribute
    val dimension: Vector = coordinates.area()
    
    /** Die Form als Sammlung aus Vektoren. */
    val asVectors: Set<Vector> by lazy {coordinates.map {it - Coordinates.origin}.toSet()}
    
    /** Die Größe Der Form, als Anzahl an Feldern, die es belegt. */
    @XStreamOmitField
    val size: Int = coordinates.size
    
    // Boilerplate to make getPossibleMoves calculation faster, by removing duplicate variants
    /**
     * Eine Sammlung aller möglichen Varianten der Form, zusammen mit einer Transformation, die sie erzeugt hat.
     * @see get */
    val variants: Map<Set<Coordinates>, Pair<Rotation, Boolean>>
    
    /** Eine Map aller Transformationen sowie die entsprechende resultierende Variante. */
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
    
    /**
     * Index Operator, der die den Parametern entsprechende Variation zurückgibt.
     * Syntax: PieceShape[Rotation, shouldFlip]
     * @param rotation um wie viel die Form rotiert werden soll
     * @param shouldFlip ob die Form entlang der y-Achse gespiegelt werden soll
     * @return Ein Set an [Coordinates], welches die entsprechend gedrehte Variante der ursprünglichen Form ist.
     */
    operator fun get(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            transform(rotation, shouldFlip)
    
    /** Transformiert die Form entsprechend.
     * @see get */
    fun transform(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            transformations[rotation to shouldFlip] ?: emptySet()
    
    /** Berechnet die gewollte Transformation. */
    fun legacyTransform(rotation: Rotation, shouldFlip: Boolean): Set<Coordinates> =
            coordinates.rotate(rotation).flip(shouldFlip)
    
    companion object {
        /** Eine Map, die anhand des Index' des Enums die entsprechende Form zurückgibt. */
        val shapes: Map<Int, PieceShape> = ((0 until Constants.TOTAL_PIECE_SHAPES) zip values()).toMap()
    }
}
