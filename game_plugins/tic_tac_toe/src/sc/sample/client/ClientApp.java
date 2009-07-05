package sc.sample.client;

import java.io.IOException;

import com.thoughtworks.xstream.XStream;

public class ClientApp
{
	public static void main(String[] args) throws IOException
	{
		SimpleClient client = new SimpleClient(new XStream());
		client.joinAnyGame();
	}
}
