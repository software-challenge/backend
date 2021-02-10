package sc.server.plugins

import sc.shared.IWinReason

enum class TestWinReason(override val message: String): IWinReason {
    WIN("%s won");
}