package gameClient;

public class Starter
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

		System.out.println(args);

		for (int i = 0; i < args.length; i++)
		{
			if ((i + 1) < args.length)
			{
				if (args[i].equals("--host"))
				{
					host = args[i + 1];

				}
				else if (args[i].equals("--port"))
				{
					port = Integer.valueOf(args[i + 1]);
				}
				else if (args[i].equals("--reservation"))
				{
					reservierung = args[i + 1];
				}
			}
		}

		new SimpleClient(host, port, reservierung);
	}
}
