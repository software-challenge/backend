package sc.server.gaming

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.maps.shouldContainExactly
import org.junit.jupiter.api.assertThrows
import sc.api.plugins.Team
import sc.framework.plugins.Constants
import sc.protocol.requests.PrepareGameRequest
import sc.server.Configuration
import sc.server.helpers.StringNetworkInterface
import sc.server.network.Client
import sc.server.plugins.TestGame
import sc.server.plugins.TestPlugin
import sc.shared.*
import java.io.StringWriter

val minimalReplay = """
    <protocol>
    <room roomId="PLACEHOLDERID">
      <data class="memento">
        <state class="sc.server.plugins.TestGameState">
          <turn>0</turn>
          <state>0</state>
          <red name="Fred" team="ONE"/>
          <blue name="Marta" team="TWO"/>
        </state>
      </data>
    </room>
    <room roomId="PLACEHOLDERID">
      <data class="result">
        <definition>
          <fragment name="winner">
            <aggregation>SUM</aggregation>
            <relevantForRanking>true</relevantForRanking>
          </fragment>
          <fragment name="index">
            <aggregation>AVERAGE</aggregation>
            <relevantForRanking>true</relevantForRanking>
          </fragment>
          <fragment name="turn">
            <aggregation>AVERAGE</aggregation>
            <relevantForRanking>true</relevantForRanking>
          </fragment>
        </definition>
        <scores>
          <entry>
            <player name="Fred" team="ONE"/>
            <score>
              <part>0</part>
              <part>0</part>
              <part>2</part>
            </score>
          </entry>
          <entry>
            <player name="Marta" team="TWO"/>
            <score>
              <part>2</part>
              <part>1</part>
              <part>2</part>
            </score>
          </entry>
        </scores>
        <winner team="TWO" regular="true" reason="Marta won through index"/>
      </data>
    </room>
    </protocol>""".trimIndent()

class GameRoomTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    val client = Client(StringNetworkInterface("")).apply { start() }
    "A GameRoomManager" should {
        val manager = GameRoomManager()
        Configuration.set(Configuration.SAVE_REPLAY, true)
        "create a game when a player joins" {
            manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).playerCount shouldBe 1
            manager.games shouldHaveSize 1
        }
        val room = manager.games.single()
        "add a second player to the existing game" {
            manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).playerCount shouldBe 2
        }
        "return correct scores on game over" {
            val game = (room.game as TestGame)
            game.currentState.turn = 2
            val playersScores = game.players.associateWith { PlayerScore(it.team.index * Constants.WIN_SCORE, it.team.index, 2) }
            room.onGameOver(game.getResult())
            room.result.isRegular shouldBe true
            room.result.scores shouldContainExactly playersScores
            room.result.win?.winner shouldBe Team.TWO
            room.isOver shouldBe true
        }
        "save a correct replay" {
            val replayWriter = StringWriter()
            room.saveReplay(replayWriter)
            replayWriter.toString() shouldBe minimalReplay.replace("PLACEHOLDERID", room.id)
        }
    }
    "A GameRoom with prepared reservations" should {
        val manager = GameRoomManager()
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
