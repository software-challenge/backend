package sc.api.plugins

import sc.framework.PublicCloneable

interface IField<FIELD: IField<FIELD>>: PublicCloneable<FIELD> {
    val isEmpty: Boolean
}