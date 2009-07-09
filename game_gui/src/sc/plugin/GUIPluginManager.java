package sc.plugin;

import sc.logic.GUIConfiguration;
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
}
