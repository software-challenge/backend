package sc.server.plugins;

import sc.plugins.IPlugin;
import sc.plugins.PluginDescriptor;

public class PluginInstance<PluginType extends IPlugin> {
  private final Class<? extends PluginType> definition;
  private final PluginDescriptor description;

  private PluginType instance;

  public PluginInstance(Class<? extends PluginType> definition) {
    this.definition = definition;
    this.description = definition.getAnnotation(PluginDescriptor.class);
  }

  public PluginInstance(PluginType instance) {
    this((Class<? extends PluginType>)instance.getClass());
    this.instance = instance;
  }

  @SuppressWarnings("unchecked")
  private Class<? extends PluginType> uncheckedDefinitionCast(
          Class<?> definition) throws ClassCastException {
    return (Class<? extends PluginType>) definition;
  }

  public PluginType getPlugin() {
    return this.instance;
  }

  public PluginDescriptor getDescription() {
    return this.description;
  }

  public void load() throws PluginLoaderException {
    if (instance == null) {
      if(definition == null)
        throw new IllegalStateException("Plugin instance and definition are null!");
      this.instantiate();
    }
    this.instance.initialize();
  }

  public void unload() {
    this.instance.unload();
  }

  private void instantiate() throws PluginLoaderException {
    try {
      Class<? extends PluginType> castedDefintion = uncheckedDefinitionCast(this.definition);
      this.instance = castedDefintion.newInstance();
    } catch (IllegalAccessException | ClassCastException e) {
      throw new PluginLoaderException(e);
    } catch (InstantiationException e) {
      throw new PluginLoaderException(
              "Could not instantiate the plugin (" + this.definition.getCanonicalName() + "). "
                      + "Plugin must be a class with a public parameterless constructor and must not be nested.",
              e);
    }
  }

}
