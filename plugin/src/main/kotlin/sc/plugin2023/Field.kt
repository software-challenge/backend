package sc.plugin2023

import sc.api.plugins.IField
import sc.api.plugins.Team

data class Field(val fish: Int = 0, val penguin: Team? = null) : IField<Field> {
    override val isEmpty: Boolean
        get() = fish == 0 && penguin == null
    override val isOccupied: Boolean
        get() = penguin != null
    
    override fun clone(): Field = Field(fish, penguin)
    
    override fun toString(): String = penguin?.letter?.toString() ?: fish.toString()
}
