package sc.api.plugins

interface IField<FIELD: IField<FIELD>> {
    val isEmpty: Boolean
    fun copy(): FIELD
}

inline fun <reified T: IField<T>> Array<Array<T>>.deepCopy(): Array<Array<T>> =
    Array(size) { row -> Array(this[row].size) { column -> this[row][column].copy() } }
