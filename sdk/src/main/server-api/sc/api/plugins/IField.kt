package sc.api.plugins

interface IField<FIELD: IField<FIELD>> {
    val isEmpty: Boolean
}
