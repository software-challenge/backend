package sc.framework

import java.io.Serializable

interface PublicCloneable<T: Cloneable>: Cloneable, Serializable {
    /** Eine tiefe Kopie des Objekts. */
    public override fun clone(): T
}

inline fun <reified T: PublicCloneable<T>> List<T>.clone() =
    List(size) { this[it].clone() }

inline fun <reified T: PublicCloneable<T>> List<List<T>>.deepCopy() =
    List(size) { row -> List(this[row].size) { column -> this[row][column].clone() } }

inline fun <reified T: PublicCloneable<T>> Array<Array<T>>.deepCopy() =
    Array(size) { row -> Array(this[row].size) { column -> this[row][column].clone() } }