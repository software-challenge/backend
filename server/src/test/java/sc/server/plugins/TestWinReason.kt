package sc.server.plugins

import sc.shared.IWinReason

enum class TestWinReason(override val message: String, override val isRegular: Boolean): IWinReason {
    WIN("%s won", true);
}