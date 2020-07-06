package sc.plugin2021

import sc.api.plugins.IField

enum class FieldContent(val letter: Char) {
    EMPTY('-'),
    RED('R'),
    GREEN('G'),
    BLUE('B'),
    YELLOW('Y');
    
    override fun toString(): String = letter.toString()
}

class Field(val coordinates: Coordinates, val content: FieldContent): IField {
    override fun toString(): String = "'$content $coordinates'"
    
    override fun equals(other: Any?): Boolean {
        return other is Field &&
                other.coordinates == coordinates &&
                other.content == content
    }
}