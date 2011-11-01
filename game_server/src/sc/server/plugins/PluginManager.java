package sc.server.plugins;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugins.PluginDescriptor;
import sc.server.Configuration;

/**
 * The <code>PluginManager</code> loads all available plugins from the plugin-
 * directory.
 * 
 * @author mja
 * @author rra
 */
public abstract class PluginManager<PluginInstanceType extends PluginInstance<?, ?>>
{
	private static final String							PLUGIN_DIRECTORY			= "./plugins";
	private static final String							JAR_FILE_IDENTIFIER			= ".jar";
	private static final String							COMPILED_CLASS_IDENTIFIER	= ".class";

	protected static final Logger						logger						= LoggerFactory
																							.getLogger(PluginManager.class);

	private static final Class<? extends Annotation>	PLUGIN_ANNOTATION			= PluginDescriptor.class;

	private final Collection<PluginInstanceType>		availablePlugins			= new LinkedList<PluginInstanceType>();
	private final Collection<PluginInstanceType>		activePlugins				= new LinkedList<PluginInstanceType>();

	public synchronized void reload()
	{
		unload();

		for (URI jarURI : findPluginArchives(this.getPluginFolder()))
		{
			for (Class<?> definition : findEntryPointsInJar(jarURI))
			{
				System.out.println("Loading: " + definition.getAnnotation(PluginDescriptor.class).name());
				this.availablePlugins.add(createPluginInstance(definition, jarURI));
			}
		}

		logger.info("Plugin-Cache reloaded. {} plugins available.",
				this.availablePlugins.size());
	}

	private void unload()
	{
		for (PluginInstanceType plugin : this.activePlugins)
		{
			plugin.unload();
		}

		this.activePlugins.clear();
		this.availablePlugins.clear();
	}

	protected abstract PluginInstanceType createPluginInstance(
			Class<?> definition, URI jarURI);

	public Collection<PluginInstanceType> getAvailablePlugins()
	{
		return this.availablePlugins;
	}

	private static Collection<URI> findPluginArchives(String path)
	{
		Collection<URI> pluginArchives = new LinkedList<URI>();
		File moduleDirectory = new File(path);

		if (moduleDirectory.exists() && moduleDirectory.isDirectory())
		{
			logger.info("Loading plugins from: {}", moduleDirectory
					.getAbsoluteFile());

			for (String file : moduleDirectory.list())
			{
				if (file.endsWith(JAR_FILE_IDENTIFIER))
				{
					File jarArchiveFile = new File(moduleDirectory, file);
					pluginArchives.add(jarArchiveFile.toURI());
				}
			}
		}
		else
		{
			logger.warn("Couldn't find plugin directory: {}", moduleDirectory
					.getAbsolutePath());
		}

		return pluginArchives;
	}

	private static String getClassNameFromJarEntry(JarEntry entry)
	{
		String className = entry.getName().replace("/", ".");
		return className.substring(0, className.length()
				- COMPILED_CLASS_IDENTIFIER.length());
	}

	private Collection<Class<?>> findEntryPointsInJar(URI jarURI)
	{
		Collection<Class<?>> entryPoints = new LinkedList<Class<?>>();

		try
		{
			addJarToClassloader(jarURI.toURL());
			
			// FIXME: shouldn't have a reference to Configuration
			ClassLoader loader = Configuration.getXStream().getClassLoader();

			JarFile jarArchive = new JarFile(new File(jarURI));
			Enumeration<JarEntry> jarEntries = jarArchive.entries();

			while (jarEntries.hasMoreElements())
			{
				JarEntry entry = jarEntries.nextElement();
				if (entry.getName().endsWith(COMPILED_CLASS_IDENTIFIER))
				{
					String className = getClassNameFromJarEntry(entry);

					try
					{
						Class<?> clazz = loader.loadClass(className);
						
						if (isValidPlugin(clazz))
						{
							entryPoints.add(clazz);
						}
					}
					catch (ClassNotFoundException e)
					{
						logger
								.error(
										"Failed to load class {} from Jar (missing dependencies?): {}",
										className, e.getMessage());
					}
					catch (NoClassDefFoundError e)
					{
						logger
								.error(
										"Failed to load class {} from Jar (missing dependencies?): {}",
										className, e.getMessage());
					}
				}
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return entryPoints;
	}

	protected abstract void addJarToClassloader(URL url);

	private boolean isValidPlugin(Class<?> clazz)
	{
		return (clazz.getAnnotation(PLUGIN_ANNOTATION) != null)
				&& getPluginInterface().isAssignableFrom(clazz);
	}

	protected abstract Class<?> getPluginInterface();

	public Collection<PluginInstanceType> getActivePlugins()
	{
		return this.activePlugins;
	}

	protected void addPlugin(PluginInstanceType type)
	{
		this.availablePlugins.add(type);
		this.activePlugins.add(type);
	}

	public String getPluginFolder()
	{
		return PLUGIN_DIRECTORY;
	}
}
