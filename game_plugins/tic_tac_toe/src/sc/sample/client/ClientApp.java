package sc.sample.client;

import java.io.IOException;

public class ClientApp
{
	public static void main(String[] args) throws IOException
	{
		SimpleClient client = new SimpleClient();
		client.joinAnyGame();
	}
}
