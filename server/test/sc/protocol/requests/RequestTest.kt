package sc.protocol.requests

import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import sc.framework.plugins.RoundBasedGameInstance
import sc.helpers.xStream
import sc.networking.clients.LobbyClient
import sc.server.Configuration
import sc.server.client.PlayerListener
import sc.server.client.TestLobbyClientListener
import sc.server.client.TestObserverListener
import sc.server.client.TestPreparedGameResponseListener
import sc.server.gaming.GameRoom
import sc.server.gaming.ObserverRole
import sc.server.helpers.TestHelper
import sc.server.network.RealServerTest
import sc.server.plugins.TestMove
import sc.server.plugins.TestPlugin
import sc.server.plugins.TestTurnRequest
import sc.shared.WelcomeMessage

private const val PASSWORD = "TEST_PASSWORD"

class RequestTest: RealServerTest() {
    private lateinit var player1: LobbyClient
    private lateinit var player2: LobbyClient
    private lateinit var player3: LobbyClient
    
    @Before
    fun prepare() {
        player1 = connectClient("localhost", serverPort)
        TestHelper.waitMillis(200)
        player2 = connectClient("localhost", serverPort)
        TestHelper.waitMillis(200)
        player3 = connectClient("localhost", serverPort)
        TestHelper.waitMillis(200)
    }
    
    @Test
    fun joinRoomRequest() {
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        
        TestHelper.assertEqualsWithTimeout(1, { lobby.games.size })
        assertEquals(1, lobby.games.iterator().next().clients.size.toLong())
    }
    
    @Test
    fun authenticationRequest() {
        player1.authenticate(PASSWORD)
        TestHelper.waitMillis(200)
        val clients = lobby.clientManager.clients
        assertTrue(clients[0].isAdministrator)
        assertEquals(3, lobby.clientManager.clients.size.toLong())
        
        player2.authenticate("PASSWORD_FAIL_TEST")
        TestHelper.waitMillis(200)
        
        //Player2 got kicked
        assertEquals(2, lobby.clientManager.clients.size.toLong())
        assertFalse(clients[1].isAdministrator)
    }
    
    @Test
    fun prepareRoomRequest() {
        player1.authenticate(PASSWORD)
        player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID, true)
        val listener = TestPreparedGameResponseListener()
        player1.addListener(listener)
        
        TestHelper.waitMillis(200)
        assertNotNull(listener.response)
        
        assertEquals(1, lobby.games.size.toLong())
        assertEquals(0, lobby.games.iterator().next().clients.size.toLong())
        assertTrue(lobby.games.iterator().next().isPauseRequested)
    }
    
    @Test
    fun joinPreparedRoomRequest() {
        player1.authenticate(PASSWORD)
        val listener = TestPreparedGameResponseListener()
        player1.addListener(listener)
        
        player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitMillis(200)
        val response = listener.response
        
        val reservation = response.reservations[0]
        player1.joinPreparedGame(reservation)
        TestHelper.waitMillis(200)
        assertEquals(1, lobby.games.iterator().next().clients.size.toLong())
        
        player2.joinPreparedGame(response.reservations[1])
        TestHelper.waitMillis(200)
        assertEquals(2, lobby.games.iterator().next().clients.size.toLong())
        
        player3.joinPreparedGame(response.reservations[1])
        TestHelper.waitMillis(200)
        assertEquals(2, lobby.clientManager.clients.size.toLong())
    }
    
    @Test
    fun observationRequest() {
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        
        TestHelper.waitMillis(200)
        
        val gameRoom = lobby.games.iterator().next()
        player3.addListener(TestObserverListener())
        player3.authenticate(PASSWORD)
        player3.observe(gameRoom.id)
        
        TestHelper.waitMillis(200)
        
        val roles = lobby.clientManager.clients[2].roles.iterator()
        var hasRole = false
        while (roles.hasNext()) {
            if (roles.next() is ObserverRole) {
                hasRole = true
            }
        }
        assertTrue(hasRole)
    }
    
    @Test
    fun stepRequestException() {
        val admin = player1
        val player1 = this.player2
        val player2 = this.player3
        val p1Listener = PlayerListener()
        val p2Listener = PlayerListener()
        
        // Make player1 Admin and prepare a game in paused mode
        admin.authenticate(PASSWORD)
        val listener = TestLobbyClientListener()
        admin.addListener(listener)
        
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitMillis(500)
        
        // Room was created
        val room = lobby.games.iterator().next()
        val sp1 = room.slots[0].role.player
        sp1.addPlayerListener(p1Listener)
        admin.send(PauseGameRequest(room.id, true))
        admin.observe(room.id, false)
        
        // Wait for admin
        TestHelper.waitUntilTrue({ listener.observedReceived }, 2000)
        
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitMillis(500)
        room.slots[1].role.player.addPlayerListener(p2Listener)
        
        // Wait for the server to register that
        TestHelper.waitUntilTrue({ room.isPauseRequested }, 2000)
        
        // Wait for it to register
        // no state will be send if game is paused TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000);
        listener.newStateReceived = false
        
        TestHelper.waitUntilTrue({ p1Listener.playerEventReceived }, 2000)
        p1Listener.playerEventReceived = false
        assertEquals(p1Listener.requests.size.toLong(), 1)
        assertEquals(p1Listener.requests[0].javaClass, WelcomeMessage::class.java)
        
        player1.sendMessageToRoom(room.id, TestMove(1))
        TestHelper.waitMillis(100)
        assertEquals(room.status, GameRoom.GameStatus.OVER)
    }
    
    @Test
    fun stepRequest() {
        val admin = player1
        val player1 = this.player2
        val player2 = this.player3
        val p1Listener = PlayerListener()
        val p2Listener = PlayerListener()
        
        // Make player1 Admin and prepare a game in paused mode
        admin.authenticate(PASSWORD)
        val listener = TestLobbyClientListener()
        admin.addListener(listener)
        
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitMillis(500)
        
        // Room was created
        val room = lobby.games.iterator().next()
        val sp1 = room.slots[0].role.player
        sp1.addPlayerListener(p1Listener)
        admin.send(PauseGameRequest(room.id, true))
        admin.observe(room.id)
        
        // Wait for admin
        TestHelper.waitUntilTrue({ listener.observedReceived }, 2000)
        
        
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitMillis(500)
        room.slots[1].role.player.addPlayerListener(p2Listener)
        
        // Wait for the server to register that
        TestHelper.waitUntilTrue({ room.isPauseRequested }, 2000)
        
        // TODO the section above duplicates the one of the previous test, clean that up
        
        // Wait for it to register
        // no state will be send if game is paused TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000);
        listener.newStateReceived = false
        
        assertTrue(TestHelper.waitUntilTrue({ p1Listener.playerEventReceived }, 2000))
        p1Listener.playerEventReceived = false
        assertEquals(p1Listener.requests.size.toLong(), 1)
        assertEquals(p1Listener.requests[0].javaClass, WelcomeMessage::class.java)
        
        // enabling this should result in a GameLogicException
        // player1.sendMessageToRoom(room.getId(), new TestMove(1));
        // TestHelper.waitMillis(100);
        
        /* FIXME bugged - see Issue #124
        // Request a move from the first player
        admin.send(new StepRequest(room.getId()));
        TestHelper.waitUntilTrue(() -> listener.newStateReceived, 2000);
        // send move
        player1.sendMessageToRoom(room.getId(), new TestMove(1));
        listener.newStateReceived = false;

        admin.send(new StepRequest(room.getId()));
        // Wait for second players turn
        TestHelper.waitUntilTrue(() -> p2Listener.playerEventReceived, 4000);
        p2Listener.playerEventReceived = false;

        // Second player sends Move with value 42
        player2.sendMessageToRoom(room.getId(), new TestMove(42));
        TestHelper.waitMillis(100);*/
        
        // Request a move
        admin.send(StepRequest(room.id))
        TestHelper.waitMillis(100)
        
        // Should register as a new state
        TestHelper.waitUntilTrue({ listener.newStateReceived }, 2000)
        listener.newStateReceived = false
        // Wait for it to register
        TestHelper.waitUntilTrue({ p1Listener.playerEventReceived }, 2000)
        
        // Second player sends Move not being his turn
        player2.sendMessageToRoom(room.id, TestMove(73))
        TestHelper.waitUntilFalse({ listener.newStateReceived }, 1000)
        listener.newStateReceived = false
        TestHelper.waitMillis(500)
        
        // There should not come another request
        assertTrue(p1Listener.playerEventReceived)
        assertNotEquals(p2Listener.requests[p2Listener.requests.size - 1].javaClass, TestTurnRequest::class.java)
        
        // Should not result in a new game state
        assertFalse(listener.newStateReceived)
        p1Listener.playerEventReceived = false
        p2Listener.playerEventReceived = false
        listener.newStateReceived = false
        
        // Game should be deleted, because player3 send invalid move
        assertEquals(0L, lobby.games.size.toLong())
        
    }
    
    @Test
    @Ignore
    fun cancelRequest() {
        player1.authenticate(PASSWORD)
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        val listener = TestLobbyClientListener()
        player1.addListener(listener)
        
        // Wait for messages to get to server
        assertTrue(TestHelper.waitUntilTrue({ lobby.games.isNotEmpty() }, 1000))
        
        player1.send(CancelRequest(listener.roomId))
        assertTrue(TestHelper.waitUntilTrue({ lobby.games.isEmpty() }, 3000))
        assertEquals(0, lobby.games.size.toLong())
    }
    
    @Test
    fun testModeRequest() {
        player1.authenticate(PASSWORD)
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        val listener = TestLobbyClientListener()
        player1.addListener(listener)
        
        player1.send(TestModeRequest(true))
        TestHelper.assertEqualsWithTimeout("true", { Configuration.get(Configuration.TEST_MODE) }, 1000)
        
        player1.send(TestModeRequest(false))
        TestHelper.assertEqualsWithTimeout("false", { Configuration.get(Configuration.TEST_MODE) }, 1000)
    }
    
    @Test
    fun getScoreForPlayerRequest() {
        //TODO implement
    }
    
    @Test
    fun timeoutRequest() {
        player1.authenticate(PASSWORD)
        val listener = TestLobbyClientListener()
        
        player1.addListener(listener)
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        
        TestHelper.waitUntilEqual(1, { lobby.games.size }, 2000)
        var room = gameMgr.games.iterator().next()
        assertTrue(room.slots[0].role.player.canTimeout)
        val req = ControlTimeoutRequest(room.id, false, 0)
        player1.send(req)
        TestHelper.waitMillis(2000)
        room = gameMgr.games.iterator().next()
        assertFalse(room.slots[0].role.player.canTimeout)
    }
    
    @Test
    fun pauseRequest() {
        player1.authenticate(PASSWORD)
        val listener = TestLobbyClientListener()
        val p1Listener = PlayerListener()
        val p2Listener = PlayerListener()
        
        player1.addListener(listener)
        
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitUntilEqual(1, { lobby.games.size }, 2000)
        val room = gameMgr.games.iterator().next()
        room.slots[0].role.player.addPlayerListener(p1Listener)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        TestHelper.waitUntilEqual(2, { room.slots.size }, 2000)
        TestHelper.waitMillis(500)
        val splayer2 = room.slots[1].role.player
        splayer2.addPlayerListener(p2Listener)
        splayer2.displayName = "player2..."
        
        assertFalse(room.isPauseRequested)
        TestHelper.waitUntilEqual(2, { p1Listener.requests.size }, 2000)
        assertEquals(p1Listener.requests[0].javaClass, WelcomeMessage::class.java)
        TestHelper.waitMillis(500)
        assertEquals(p1Listener.requests[1].javaClass, TestTurnRequest::class.java)
        listener.newStateReceived = false
        
        player1.send(PauseGameRequest(room.id, true))
        TestHelper.waitUntilEqual(true, { room.isPauseRequested }, 2000)
        
        player1.sendMessageToRoom(room.id, TestMove(42))
        TestHelper.waitMillis(1000)
        // assert that (if the game is paused) no new gameState is send to the observers after a pending Request was received
        assertFalse(listener.newStateReceived)
        
        
        p1Listener.playerEventReceived = false
        p2Listener.playerEventReceived = false
        player1.send(PauseGameRequest(room.id, false))
        TestHelper.waitUntilEqual(false, { (room.game as RoundBasedGameInstance<*>).isPaused }, 2000)
        
        
        TestHelper.waitMillis(500)
        assertTrue(p2Listener.playerEventReceived)
        assertEquals(p2Listener.requests[p2Listener.requests.size - 1].javaClass,
                TestTurnRequest::class.java)
    }
    
}
