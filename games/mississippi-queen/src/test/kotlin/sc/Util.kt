package sc

import sc.api.plugins.Coordinates

infix fun Int.y(other: Int) = Coordinates(this, other)