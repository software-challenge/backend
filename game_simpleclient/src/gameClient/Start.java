package gameClient;

public class Start
{
	/**
	 * Diese Methode wird beim Start des Programmes aufgerufen. Die Parameter
	 * werden für das automatische Verbinden benötigt.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		String host = "";
		int port = 0;
		String reservierung = "";

		for (String arg : args)
		{
			if (arg.contains("--host"))
			{
				host = arg.substring(arg.indexOf(" ") + 1);
			}
			else if (arg.contains("--port"))
			{
				port = Integer.valueOf(arg.substring(arg.indexOf(" ") + 1));
			}
			else if (arg.contains("--reservation"))
			{
				reservierung = arg.substring(arg.indexOf(" ") + 1);
			}
		}

		new SimpleClient(host, port, reservierung);
	}
}
