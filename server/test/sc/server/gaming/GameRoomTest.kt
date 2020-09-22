package sc.server.gaming

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import sc.protocol.requests.PrepareGameRequest
import sc.server.Configuration
import sc.server.helpers.StringNetworkInterface
import sc.server.network.Client
import sc.server.plugins.TestPlugin

class GameRoomTest: StringSpec({
    val stringInterface = StringNetworkInterface("")
    val client = Client(stringInterface, Configuration.getXStream()).apply { start() }
    
    "create and join game" {
        val manager = GameRoomManager().apply { pluginManager.loadPlugin(TestPlugin::class.java, pluginApi) }
        
        manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).existing shouldBe false
        manager.games shouldHaveSize 1
        manager.joinOrCreateGame(client, TestPlugin.TEST_PLUGIN_UUID).existing shouldBe true
    }
    
    "prepare game & claim reservations" {
        val manager = GameRoomManager().apply { pluginManager.loadPlugin(TestPlugin::class.java, pluginApi) }
        
        val reservations = manager.prepareGame(PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID)).reservations
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
        // join second client
        ReservationManager.redeemReservationCode(client, reservations[1])
        room.clients shouldHaveSize 2
        // reject extra client
        room.join(client) shouldBe false
        room.clients shouldHaveSize 2
    }
})