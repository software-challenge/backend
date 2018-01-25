package sc.server.roles;

import junit.framework.Assert;

import org.junit.Test;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.server.gaming.GameRoom;
import sc.server.gaming.PlayerSlot;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.TestPlugin;
import sc.shared.InvalidGameStateException;

public class ContestTest extends AdministratorTest
{
	@Test
	public void shouldBeAbleToPrepareAndStartGame() throws RescuableClientException, InvalidGameStateException {
		MockClient admin = connectAsAdmin();
		Client player1 = connectClient();
		Client player2 = connectClient();

		this.lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(
				TestPlugin.TEST_PLUGIN_UUID)));

		Assert.assertEquals(1, this.gameMgr.getGames().size());
		GameRoom room = this.gameMgr.getGames().iterator().next();

		PrepareGameProtocolMessage response = admin
				.seekMessage(PrepareGameProtocolMessage.class);

		Assert.assertEquals(2, room.getSlots().size());

		this.lobby.onRequest(player1, new PacketCallback(new JoinPreparedRoomRequest(response
				.getReservations().get(1))));
		this.lobby.onRequest(player2, new PacketCallback(new JoinPreparedRoomRequest(response
				.getReservations().get(0))));

		Assert.assertEquals(2, room.getSlots().size());

		for (PlayerSlot slot : room.getSlots())
		{
			Assert.assertFalse(slot.isEmpty());
		}

		// Ordering should match the defined ordering
		Assert.assertEquals(player1, room.getSlots().get(1).getRole()
				.getClient());
		Assert.assertEquals(player2, room.getSlots().get(0).getRole()
				.getClient());
	}
}
