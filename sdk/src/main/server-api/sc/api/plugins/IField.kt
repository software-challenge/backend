package sc.api.plugins

import sc.framework.PublicCloneable

interface IField<FIELD: IField<FIELD>>: PublicCloneable<FIELD> {
    val isEmpty: Boolean
    /** Whether a piece of a player is occupying this field. */
    val isOccupied: Boolean
}