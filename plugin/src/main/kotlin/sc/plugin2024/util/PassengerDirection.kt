package sc.plugin2024.util

import sc.api.plugins.HexDirection
import sc.plugin2024.FieldType

enum class PassengerDirection(val type: FieldType, val direction: HexDirection) {
    PASSENGER0(FieldType.PASSENGER0, HexDirection.LEFT),
    PASSENGER1(FieldType.PASSENGER1, HexDirection.DOWN_LEFT),
    PASSENGER2(FieldType.PASSENGER2, HexDirection.DOWN_RIGHT),
    PASSENGER3(FieldType.PASSENGER3, HexDirection.RIGHT),
    PASSENGER4(FieldType.PASSENGER4, HexDirection.UP_RIGHT),
    PASSENGER5(FieldType.PASSENGER5, HexDirection.UP_LEFT);
}