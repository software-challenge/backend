package sc.api.plugins;

public interface IGamePluginHost {
	public void registerProtocolClass(Class<?> clazz);
	public void registerProtocolClass(Class<?> clazz, String alias);
}
