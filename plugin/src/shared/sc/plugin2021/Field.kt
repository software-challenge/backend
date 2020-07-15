package sc.plugin2021

import sc.api.plugins.IField

class Field(val coordinates: Coordinates, val color: Color): IField {
    override fun toString(): String = "'$color $coordinates'"
    
    override fun equals(other: Any?): Boolean {
        return other is Field &&
                other.coordinates == coordinates &&
                other.color == color
    }
    
    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}