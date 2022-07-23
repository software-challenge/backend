package sc.framework

interface PublicCloneable<T: Cloneable>: Cloneable {
    /** Eine tiefe Kopie des Objekts. */
    public override fun clone(): T
}