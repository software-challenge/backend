package sc.plugins;


public interface IPlugin {
  void initialize();

  default void unload() {};
}
