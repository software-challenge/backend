package sc.plugin;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.logic.GUIConfiguration;
import sc.server.Configuration;
import sc.server.plugins.GamePluginInstance;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.PluginManager;

public class GUIPluginManager extends PluginManager<GUIPluginInstance> {

	/**
	 * Singleton instance
	 */
	private static volatile GUIPluginManager instance;

	private GUIPluginManager() { // Singleton
	}

	public static GUIPluginManager getInstance() {
		if (null == instance) {
			synchronized (GUIPluginManager.class) {
				if (null == instance) {
					instance = new GUIPluginManager();
				}
			}
		}
		return instance;
	}

	protected static Logger logger = LoggerFactory
			.getLogger(GamePluginInstance.class);

	public void activateAllPlugins(IGuiPluginHost host) {
		for (GUIPluginInstance plugins : getAvailablePlugins()) {
			try {
				plugins.load(host);
			} catch (PluginLoaderException e) {
				logger.error("Failed to load plugin.", e);
			}
		}
	}

	@Override
	protected GUIPluginInstance createPluginInstance(Class<?> definition) {
		GUIPluginInstance instance = new GUIPluginInstance(definition);
		logger.info("GUIPluginInstance '{}' {{}} was loaded.", instance
				.getDescription().name(), instance.getDescription().uuid());
		return instance;
	}

	@Override
	public String getPluginFolder() {
		if (GUIConfiguration.getPluginFolder() == null) {
			return super.getPluginFolder();
		} else {
			return GUIConfiguration.getPluginFolder();
		}
	}

	@Override
	protected Class<?> getPluginInterface() {
		return IGuiPlugin.class;
	}

	@Override
	protected void addJarToClassloader(URL url) {
		Configuration.addXStreamClassloaderURL(url);
	}
}
