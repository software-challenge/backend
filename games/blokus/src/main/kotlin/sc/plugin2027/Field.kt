package sc.plugin2027

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.IField
import sc.framework.PublicCloneable

/**
 * Beschreibt die Farbe eines bestimmten Felds.
 * @property coordinates die Position des Felds, als [Coordinates]
 * @property content Die Farbe des Felds, als [FieldContent] oder [Color]
 */
@XStreamAlias(value = "field")
data class Field(val coordinates: Coordinates, var content: FieldContent): IField, PublicCloneable<Field> {
    
    constructor(coordinates: Coordinates, color: Color): this(coordinates, color.toFieldContent())
    
    /**
     * Gibt zurück, ob das Feld leer ist, d.h. keinen Inhalt hat.
     *
     * @return true, wenn das Feld leer ist, sonst false
     */
    override val isEmpty = content == FieldContent.EMPTY
    
    override fun clone(): Field {
        return deepCopy()
    }
    
    override fun deepCopy(): Field {
        return Field(coordinates.copy(), content)
    }
    
    override fun equals(other: Any?): Boolean {
        return other is Field && this.coordinates == other.coordinates && this.content == other.content
    }
    
}
