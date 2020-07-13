package sc.plugin2021

import sc.api.plugins.IField

// TODO: replace this by elements from the Color class
enum class FieldContent(val letter: Char) {
    EMPTY('-'),
    BLUE('B'),
    YELLOW('Y'),
    RED('R'),
    GREEN('G');
    
    override fun toString(): String = letter.toString()
}

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