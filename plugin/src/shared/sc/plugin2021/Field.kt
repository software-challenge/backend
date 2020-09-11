package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField

@XStreamAlias(value = "field")
class Field(val coordinates: Coordinates, val content: FieldContent): IField {
    override fun toString(): String = "'$content $coordinates'"
    
    override fun equals(other: Any?): Boolean {
        return other is Field &&
                other.coordinates == coordinates &&
                other.content == content
    }
    
    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }
}