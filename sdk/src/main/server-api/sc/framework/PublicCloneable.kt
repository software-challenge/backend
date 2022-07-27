package sc.framework

import java.io.Serializable

interface PublicCloneable<T: Cloneable>: Cloneable, Serializable {
    /** Eine tiefe Kopie des Objekts. */
    public override fun clone(): T
}