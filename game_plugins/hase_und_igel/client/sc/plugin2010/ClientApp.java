package sc.plugin2010;

import java.io.IOException;

import com.thoughtworks.xstream.XStream;

public class ClientApp
{
	public static void main(String[] args) throws IOException
	{
		Client client = new Client("Hase und Igel", new XStream(), "localhost", 13050);
		client.joinAnyGame();
	}
}
