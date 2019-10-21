package sc.plugins;


public interface IPlugin<HostType> {
  void initialize(HostType host);

  /** Open handles should be */
  void unload();
}
