package sc.plugin2021

import sc.api.plugins.IField

enum class FieldContent(val letter: Char) {
    EMPTY('-'),
    RED('R'),
    GREEN('G'),
    BLUE('B'),
    YELLOW('Y')
}

class Field(val coordinates: Coordinates, val content: FieldContent): IField