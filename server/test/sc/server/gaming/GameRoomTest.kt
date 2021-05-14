package sc.server.gaming

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import sc.protocol.requests.PrepareGameRequest
import sc.server.helpers.StringNetworkInterface
import sc.server.network.Client
import sc.server.plugins.TestPlugin
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import sc.shared.SlotDescriptor
import java.io.StringWriter

class GameRoomTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    val client = Client(StringNetworkInterface("")).apply { start() }
    "A GameRoomManager" should {
        val manager = GameRoomManager().apply { pluginManager.loadPlugin(TestPlugin::class.java) }
        "create a game when a player joins" {
            manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).playerCount shouldBe 1
            manager.games shouldHaveSize 1
        }
        val room = manager.games.single()
        "add a second player to the existing game" {
            manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).playerCount shouldBe 2
        }
        "return correct scores on game over" {
            val playersScores = room.game.players.associateWith { PlayerScore(ScoreCause.REGULAR, "Game terminated", 0) }
            room.onGameOver(playersScores)
            room.result.isRegular shouldBe true
            room.result.scores shouldContainExactly playersScores.values
            room.isOver shouldBe true
        }
        "save a correct replay" {
            val replayWriter = StringWriter()
            room.saveReplay(replayWriter)
            replayWriter.toString() shouldBe """
                <protocol>
                <room roomId="${room.id}">
                  <data class="memento">
                    <state class="sc.server.plugins.TestGameState">
                      <red displayName="">
                        <color class="sc.server.helpers.TestTeam">RED</color>
                      </red>
                      <blue displayName="">
                        <color class="sc.server.helpers.TestTeam">BLUE</color>
                      </blue>
                      <turn>0</turn>
                      <state>0</state>
                      <currentPlayer>RED</currentPlayer>
                      <startPlayer>RED</startPlayer>
                    </state>
                  </data>
                </room>
                <room roomId="${room.id}">
                  <data class="result">
                    <definition>
                      <fragment name="winner">
                        <aggregation>SUM</aggregation>
                        <relevantForRanking>true</relevantForRanking>
                      </fragment>
                    </definition>
                    <score cause="REGULAR" reason="Game terminated">
                      <part>0</part>
                    </score>
                    <score cause="REGULAR" reason="Game terminated">
                      <part>0</part>
                    </score>
                  </data>
                </room>
                </protocol>
            """.trimIndent()
        }
    }
    "A GameRoom with prepared reservations" should {
        val manager = GameRoomManager().apply { pluginManager.loadPlugin(TestPlugin::class.java) }
        val player2name = "opponent"
        
        val reservations = manager.prepareGame(PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID, descriptor2 = SlotDescriptor(player2name))).reservations
        manager.games shouldHaveSize 1
        val room = manager.games.single()
        room.clients shouldHaveSize 0
        "reject a client with wrong or no reservation" {
            assertThrows<UnknownReservationException> {
                ReservationManager.redeemReservationCode(client, "nope")
            }
            room.join(client) shouldBe false
            room.clients shouldHaveSize 0
        }
        "join a client with reservation" {
            ReservationManager.redeemReservationCode(client, reservations[0])
            room.clients shouldHaveSize 1
        }
        "not accept a reservation twice" {
            assertThrows<UnknownReservationException> {
                ReservationManager.redeemReservationCode(client, reservations[0])
            }
            room.clients shouldHaveSize 1
        }
        "accept a second client and create Players" {
            ReservationManager.redeemReservationCode(client, reservations[1])
            room.clients shouldHaveSize 2
        }
        "reject a third client" {
            room.join(client) shouldBe false
            room.clients shouldHaveSize 2
        }
        "have properly named players" {
            room.game.players[0].displayName shouldBe "Player1"
            room.game.players[1].displayName shouldBe player2name
        }
    }
})
