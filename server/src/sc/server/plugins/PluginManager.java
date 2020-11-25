package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.plugins.IPlugin;
import sc.plugins.PluginDescriptor;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/** The <code>PluginManager</code> loads all available plugins from the plugin directory. */
public abstract class PluginManager<PluginType extends IPlugin, PluginInstanceType extends PluginInstance<PluginType>> {

  protected static final Logger logger = LoggerFactory.getLogger(PluginManager.class);

  private static final Class<? extends Annotation> PLUGIN_ANNOTATION = PluginDescriptor.class;

  protected final Collection<PluginInstanceType> availablePlugins = new ArrayList<>(2);
  protected final Collection<PluginInstanceType> activePlugins = new ArrayList<>(2);

  public abstract void reload();

  private void unload() {
    for (PluginInstanceType plugin : this.activePlugins) {
      plugin.unload();
    }

    this.activePlugins.clear();
    this.availablePlugins.clear();
  }

  protected abstract PluginInstanceType createPluginInstance(Class<? extends PluginType> definition, URI jarURI);

  public Collection<PluginInstanceType> getAvailablePlugins() {
    return this.availablePlugins;
  }

  protected abstract Class<? extends PluginType> getPluginInterface();

  public Collection<PluginInstanceType> getActivePlugins() {
    return this.activePlugins;
  }

  protected void addPlugin(PluginInstanceType type) {
    this.availablePlugins.add(type);
    this.activePlugins.add(type);
  }

}
