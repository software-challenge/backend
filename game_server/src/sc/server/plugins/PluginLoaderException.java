package sc.server.plugins;

public class PluginLoaderException extends Exception
{
	/**  */
	private static final long	serialVersionUID = -8356437408824330176L;

	public PluginLoaderException(Throwable e)
	{
		super(e);
	}

	public PluginLoaderException(String message, InstantiationException e)
	{
		super(message, e);
	}

}
