package sc.plugin;

import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.server.plugins.PluginInstance;

public class GUIPluginInstance extends PluginInstance<IGuiPluginHost, IGuiPlugin> {

	public GUIPluginInstance(Class<?> definition) {
		super(definition);
	}

}
