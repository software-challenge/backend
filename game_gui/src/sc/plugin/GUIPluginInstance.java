package sc.plugin;

import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.server.plugins.PluginInstance;

public class GUIPluginInstance extends
		PluginInstance<IGuiPluginHost, IGuiPlugin> {

	private final String version;

	public GUIPluginInstance(Class<?> definition, String version) {
		super(definition);

		if (version == null) {
			this.version = "Unbekannt";
		} else {
			this.version = version;
		}
	}

	public String getVersion() {
		return version;
	}

}
