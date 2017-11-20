package sc.server.plugins;

import sc.plugins.IPlugin;
import sc.plugins.PluginDescriptor;

public class PluginInstance<HostType, PluginType extends IPlugin<HostType>>
{
	private final Class<?>			definition;
	private PluginType				instance;
	private final PluginDescriptor	description;

	public PluginInstance(Class<?> definition)
	{
		this.definition = definition;
		this.description = definition.getAnnotation(PluginDescriptor.class);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends PluginType> uncheckedDefinitionCast(
			Class<?> definition) throws ClassCastException
	{
		return (Class<? extends PluginType>) definition;
	}

	public PluginType getPlugin()
	{
		return this.instance;
	}

	public PluginDescriptor getDescription()
	{
		return this.description;
	}

	public void load(HostType host) throws PluginLoaderException
	{
		this.instantiate();
		this.instance.initialize(host);
	}

	public void unload()
	{
		this.instance.unload();
	}

	private void instantiate() throws PluginLoaderException
	{
		try
		{
			Class<? extends PluginType> castedDefintion = uncheckedDefinitionCast(this.definition);
			this.instance = castedDefintion.newInstance();
		}
		catch (IllegalAccessException e)
		{
			throw new PluginLoaderException(e);
		}
		catch (InstantiationException e)
		{
			throw new PluginLoaderException(
					"Could not instanciate the plugin ("
							+ this.definition.getCanonicalName()
							+ "). "
							+ "Plugin must be a class with a public parameterless constructor and must not be nested.",
					e);
		}
		catch (ClassCastException e)
		{
			throw new PluginLoaderException(e);
		}
	}
}
