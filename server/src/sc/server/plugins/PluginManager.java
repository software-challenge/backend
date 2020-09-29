package sc.server.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.plugins.PluginDescriptor;
import sc.server.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** The <code>PluginManager</code> loads all available plugins from the plugin directory. */
public abstract class PluginManager<PluginInstanceType extends PluginInstance<?>> {

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

  protected abstract PluginInstanceType createPluginInstance(Class<?> definition, URI jarURI);

  public Collection<PluginInstanceType> getAvailablePlugins() {
    return this.availablePlugins;
  }

  protected abstract Class<?> getPluginInterface();

  public Collection<PluginInstanceType> getActivePlugins() {
    return this.activePlugins;
  }

  protected void addPlugin(PluginInstanceType type) {
    this.availablePlugins.add(type);
    this.activePlugins.add(type);
  }

}
