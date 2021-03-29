package sc.server.plugins

import sc.shared.IMoveMistake

enum class TestMoveMistake(override val message: String): IMoveMistake {
    INVALID_FORMAT("Received message wasn't recognised as move");
}
