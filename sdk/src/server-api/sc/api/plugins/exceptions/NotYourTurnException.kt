package sc.api.plugins.exceptions

import sc.framework.plugins.Player
import sc.protocol.room.RoomMessage

data class NotYourTurnException(
        val expected: Player?,
        val actual: Player,
        val data: RoomMessage
): GameLogicException("It's not your turn yet; expected: $expected, got $actual (msg was $data).")