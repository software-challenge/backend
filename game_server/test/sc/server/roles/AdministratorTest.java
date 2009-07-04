package sc.server.roles;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import sc.api.plugins.RescueableClientException;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.TestPlugin;

public class AdministratorTest extends AbstractRoleTest
{
	private static final String	CORRECT_PASSWORD	= "this-is-a-secret";
	private static final String	WRONG_PASSWORD		= "i-am-a-hacker";

	@Test
	public void shouldBecomeAdminWithCorrectPassword() throws IOException,
			RescueableClientException
	{
		Client client = connectAsAdmin();

		Assert.assertEquals(1, client.getRoles().size());
		Assert.assertEquals(true, client.isAdministrator());
	}

	@Test
	public void shouldNotBecomeAdminWithWrongPassword() throws IOException
	{
		Client client = connectClient();

		Configuration.set(Configuration.PASSWORD_KEY, CORRECT_PASSWORD);

		try
		{
			lobby.onRequest(client, new PacketCallback(new AuthenticateRequest(
					WRONG_PASSWORD)));
			Assert.fail("No exception was thrown");
		}
		catch (RescueableClientException e)
		{
			// expected
		}

		Assert.assertEquals(0, client.getRoles().size());
		Assert.assertEquals(false, client.isAdministrator());
	}

	protected MockClient connectAsAdmin()
	{
		MockClient client = null;
		client = connectClient();

		Configuration.set(Configuration.PASSWORD_KEY, CORRECT_PASSWORD);

		try
		{
			lobby.onRequest(client, new PacketCallback(new AuthenticateRequest(
					CORRECT_PASSWORD)));
		}
		catch (RescueableClientException e)
		{
			Assert.fail("Could not authenticate as admin.");
		}

		return client;
	}

	@Test
	public void shouldBeAbleToPrepareGame() throws RescueableClientException
	{
		Client client = connectAsAdmin();

		lobby.onRequest(client, new PacketCallback(new PrepareGameRequest(
				TestPlugin.TEST_PLUGIN_UUID, 2)));

		Assert.assertEquals(1, gameMgr.getGames().size());
	}
}
