package sc.server.roles;

import junit.framework.Assert;

import org.junit.Test;

import sc.api.plugins.RescueableClientException;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.server.gaming.GamePreparationResponse;
import sc.server.gaming.GameRoom;
import sc.server.gaming.PlayerSlot;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.TestPlugin;

public class ContestTest extends AdministratorTest
{
	@Test
	public void shouldBeAbleToPrepareGame() throws RescueableClientException
	{
		MockClient admin = connectAsAdmin();
		Client player1 = connectClient();
		Client player2 = connectClient();

		lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(
				TestPlugin.TEST_PLUGIN_UUID, 2)));

		Assert.assertEquals(1, gameMgr.getGames().size());
		GameRoom room = gameMgr.getGames().iterator().next();

		GamePreparationResponse response = admin
				.seekMessage(GamePreparationResponse.class);

		Assert.assertEquals(2, room.getSlots().size());

		lobby.onRequest(player1, new PacketCallback(new JoinPreparedRoomRequest(response
				.getReservations().get(1))));
		lobby.onRequest(player2, new PacketCallback(new JoinPreparedRoomRequest(response
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
