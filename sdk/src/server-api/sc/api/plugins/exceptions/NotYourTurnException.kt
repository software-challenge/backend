package sc.api.plugins.exceptions

import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage

data class NotYourTurnException(
        val expected: Player?,
        val actual: Player,
        val data: ProtocolMessage):
        GameLogicException("It's not your turn yet; expected: $expected, got $actual (msg was $data).")