package sc.framework

import java.io.Serializable

interface PublicCloneable<T: Cloneable>: Cloneable, DeepCloneable<T> {
    /** Eine tiefe Kopie des Objekts. */
    public override fun clone(): T
    override fun deepCopy(): T = clone()
}

interface DeepCloneable<T>: Serializable {
    /** Eine tiefe Kopie des Objekts. */
    fun deepCopy(): T
}

inline fun <reified T: DeepCloneable<T>> List<T>.clone() =
    List(size) { this[it].deepCopy() }

inline fun <reified T: DeepCloneable<T>> List<List<T>>.deepCopy() =
    List(size) { row -> List(this[row].size) { column -> this[row][column].deepCopy() } }

inline fun <reified T: DeepCloneable<T>> Array<Array<T>>.deepCopy() =
    Array(size) { row -> Array(this[row].size) { column -> this[row][column].deepCopy() } }