package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField

/**
 * Beschreibt die Farbe eines bestimmten Felds.
 * @property coordinates die Position des Felds, als [Coordinates]
 * @property content Die Farbe des Felds, als [FieldContent] oder [Color]
 */
@XStreamAlias(value = "field")
class Field(val coordinates: Coordinates, val content: FieldContent): IField {
    
    constructor(coordinates: Coordinates, content: Color): this(coordinates, +content)
    
    val isEmpty = content == FieldContent.EMPTY
    
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