package sc.server.gaming

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.nulls.*
import sc.api.plugins.Team
import sc.framework.ReplayLoader
import sc.server.plugins.TestGameState
import java.io.File
import java.util.zip.GZIPOutputStream

class ReplayLoaderTest: FunSpec({
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
                val loaded = client().loadHistory()
                loaded.first shouldBe listOf(state)
                loaded.second?.win.shouldNotBeNull().run {
                    winner shouldBe Team.TWO
                    reason.message shouldBe "Marta won through index"
                }
                client().getTurn(0) shouldBe state
                client().getTurn(-1) shouldBe state
                shouldThrow<NoSuchElementException> {
                    client().getTurn(1) shouldBe state
                }
            }
        }
        test("Inverted String") {
            val history = ReplayLoader(ReplayLoader.invert(minimalReplay).byteInputStream()).loadHistory()
            history.second?.win.shouldNotBeNull().run {
                winner shouldBe Team.ONE
                reason.message shouldBe "Marta won through index"
            }
        }
    }
})