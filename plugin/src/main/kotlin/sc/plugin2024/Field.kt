package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.Coordinates
import sc.api.plugins.HexDirection
import sc.api.plugins.IField
import sc.plugin2024.util.FieldConverter

@XStreamConverter(FieldConverter::class)
@XStreamAlias("field")
data class Field(
        val coordinate: Coordinates, var type: FieldType, val points: Int, var ship: Ship? = null,
        var segmentIndex: Int? = null, var segmentDir: HexDirection? = null,
): IField<Field> {
    override val isEmpty: Boolean
        get() = ship == null && !isBlocked
    
    fun isPassable(): Boolean {
        return setOf(FieldType.WATER, FieldType.LOG, FieldType.SANDBANK, FieldType.GOAL).contains(type)
    }
    
    val isBlocked: Boolean
        get() = type == FieldType.BLOCKED
    
    override fun clone(): Field {
        return Field(coordinate, type, points, ship?.copy(), segmentIndex, segmentDir)
    }
}
