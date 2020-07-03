package sc.plugin2021

import sc.api.plugins.IField

enum class FieldContent {
    EMPTY, RED, GREEN, BLUE, YELLOW
}

class Field(val coordinates: Coordinates, val content: FieldContent): IField