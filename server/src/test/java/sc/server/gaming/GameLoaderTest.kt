package sc.server.gaming

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.framework.ReplayLoader
import sc.server.plugins.TestGameState
import java.io.File
import java.util.zip.GZIPOutputStream

@Suppress("BlockingMethodInNonBlockingContext")
class GameLoaderTest: FunSpec({
    context("GameLoaderClient loads replay from") {
        val tmpfile = File.createTempFile("test-replay", ".xml.gz")
        GZIPOutputStream(tmpfile.outputStream(), true).also { out ->
            minimalReplay.byteInputStream().copyTo(out)
            out.close()
        }
        val state = TestGameState()
        listOf(
                "String" to { ReplayLoader(minimalReplay.byteInputStream()) },
                "GZip File" to { ReplayLoader(tmpfile) }
        ).forEach { (clue, client) ->
            test(clue) {
                client().loadHistory().first shouldBe listOf(state)
                client().getTurn(0) shouldBe state
                client().getTurn(-1) shouldBe state
                shouldThrow<NoSuchElementException> {
                    client().getTurn(1) shouldBe state
                }
            }
        }
    }
})