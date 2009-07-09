package sc.plugin;

import sc.server.plugins.PluginManager;

public class GUIPluginManager extends PluginManager<GUIPluginInstance> {

	@Override
	protected GUIPluginInstance createPluginInstance(Class<?> definition) {
		GUIPluginInstance instance = new GUIPluginInstance(definition);
		logger.info("GUIPluginInstance '{}' {{}} was loaded.", instance.getDescription()
				.name(), instance.getDescription().uuid());
		return instance;
	}

}
