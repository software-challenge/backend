package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.IField
import sc.api.plugins.Team
import sc.framework.PublicCloneable
import sc.plugin2023.util.FieldConverter

@XStreamConverter(FieldConverter::class)
@XStreamAlias("field")
data class Field(val fish: Int = 0, val penguin: Team? = null) : IField<Field>, PublicCloneable<Field> {
    override val isEmpty: Boolean
        get() = fish == 0 && penguin == null
    val isOccupied: Boolean
        get() = penguin != null
    
    override fun clone(): Field = copy()
    
    override fun toString(): String = penguin?.letter?.toString() ?: fish.toString()
}
