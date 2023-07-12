package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.Coordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.IField

@XStreamAlias("field")
data class Field(
        val coordinate: Coordinates,
        @XStreamAsAttribute var type: FieldType,
        @XStreamAsAttribute val points: Int,
        var ship: Ship? = null,
        @XStreamAsAttribute var segmentIndex: Int? = null,
        @XStreamAsAttribute var segmentDir: CubeDirection? = null,
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
