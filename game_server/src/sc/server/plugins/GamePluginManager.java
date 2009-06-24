package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.server.gaming.GamePluginApi;


public class GamePluginManager extends PluginManager<GamePluginInstance>
{
	protected static Logger	logger	= LoggerFactory
											.getLogger(GamePluginInstance.class);

	public void activateAllPlugins(GamePluginApi context)
	{
		for (GamePluginInstance plugins : getAvailablePlugins())
		{
			try
			{
				plugins.load(context);
			}
			catch (PluginLoaderException e)
			{
				logger.error("Failed to load plugin.", e);
			}
		}
	}

	@Override
	protected GamePluginInstance createPluginInstance(Class<?> definition)
	{
		return new GamePluginInstance(definition);
	}
}
