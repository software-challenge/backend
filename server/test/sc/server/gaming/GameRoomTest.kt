package sc.server.gaming

import io.kotest.core.spec.style.StringSpec
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

class GameRoomTest: StringSpec({
    val stringInterface = StringNetworkInterface("")
    val client = Client(stringInterface).apply { start() }
    
    "create, join, end game" {
        val manager = GameRoomManager().apply { pluginManager.loadPlugin(TestPlugin::class.java) }
        // TODO Replay observing
        // Configuration.set(Configuration.SAVE_REPLAY, "true")
        
        manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).existing shouldBe false
        manager.games shouldHaveSize 1
        val room = manager.games.single()
        room.game.players shouldHaveSize 1
        manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).existing shouldBe true
        
        val playersScores = room.game.players.associateWith { PlayerScore(ScoreCause.REGULAR, "Game terminated", 0) }
        room.onGameOver(playersScores)
        room.result.isRegular shouldBe true
        room.result.scores shouldContainExactly playersScores.values
        room.isOver shouldBe true
    }
    
    "prepare game & claim reservations" {
        val manager = GameRoomManager().apply { pluginManager.loadPlugin(TestPlugin::class.java) }
        val player2name = "opponent"
        
        val reservations = manager.prepareGame(PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID, descriptor2 = SlotDescriptor(player2name))).reservations
        manager.games shouldHaveSize 1
        val room = manager.games.first()
        room.clients shouldHaveSize 0
        // reject client with wrong or no reservation
        assertThrows<UnknownReservationException> {
            ReservationManager.redeemReservationCode(client, "nope")
        }
        room.join(client) shouldBe false
        room.clients shouldHaveSize 0
        // join a client
        ReservationManager.redeemReservationCode(client, reservations[0])
        room.clients shouldHaveSize 1
        // don't accept a reservation twice
        assertThrows<UnknownReservationException> {
            ReservationManager.redeemReservationCode(client, reservations[0])
        }
        room.clients shouldHaveSize 1
        room.game.players shouldHaveSize 0
        // join second client and sync
        ReservationManager.redeemReservationCode(client, reservations[1])
        room.clients shouldHaveSize 2
        room.game.players shouldHaveSize 2
        // reject extra client
        room.join(client) shouldBe false
        room.clients shouldHaveSize 2
        // check game
        room.game.players[0].displayName shouldBe "Player1"
        room.game.players[1].displayName shouldBe player2name
    }
})