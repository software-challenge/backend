package sc.server.plugins;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.PluginDescriptor;


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

	private Collection<PluginInstanceType>				availablePlugins			= new LinkedList<PluginInstanceType>();
	private Collection<PluginInstanceType>				activePlugins				= new LinkedList<PluginInstanceType>();

	public synchronized void reload()
	{
		unload();
		
		for (URI jarURI : findPluginArchives())
		{
			for (Class<?> definition : findGameDefinitionsInJar(jarURI))
			{
				this.availablePlugins.add(createPluginInstance(definition));
			}
		}

		logger.info("Plugin-Cache reloaded. {} plugins available.",
				availablePlugins.size());
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
			Class<?> definition);

	public Collection<PluginInstanceType> getAvailablePlugins()
	{
		return this.availablePlugins;
	}

	private static Collection<URI> findPluginArchives()
	{
		Collection<URI> pluginArchives = new LinkedList<URI>();
		File moduleDirectory = new File(PLUGIN_DIRECTORY);

		if (moduleDirectory.exists() && moduleDirectory.isDirectory())
		{
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

	private static Collection<Class<?>> findGameDefinitionsInJar(URI jarURI)
	{
		Collection<Class<?>> gameDefinitions = new LinkedList<Class<?>>();

		try
		{
			URLClassLoader loader = new URLClassLoader(new URL[] { jarURI
					.toURL() }, PluginManager.class.getClassLoader());

			JarFile jarArchive = new JarFile(new File(jarURI));
			Enumeration<JarEntry> jarEntries = jarArchive.entries();

			while (jarEntries.hasMoreElements())
			{
				JarEntry entry = jarEntries.nextElement();
				if (entry.getName().endsWith(COMPILED_CLASS_IDENTIFIER))
				{
					try
					{
						String className = getClassNameFromJarEntry(entry);
						Class<?> clazz = loader.loadClass(className);

						if (isValidPlugin(clazz))
						{
							gameDefinitions.add(clazz);
						}
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
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

		return gameDefinitions;
	}

	private static boolean isValidPlugin(Class<?> clazz)
	{
		return (clazz.getAnnotation(PLUGIN_ANNOTATION) != null);
	}

	public Collection<PluginInstanceType> getActivePlugins()
	{
		return this.activePlugins;
	}
}
