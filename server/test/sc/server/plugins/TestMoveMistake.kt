package sc.server.plugins

import sc.shared.IMoveMistake

enum class TestMoveMistake: IMoveMistake {
    INVALID_FORMAT {
        override fun toString(): String = "Received message wasn't recognised as move"
    };
}
