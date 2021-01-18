package sc.plugins;


public interface IPlugin {
  default void initialize() {};

  default void unload() {};
}
