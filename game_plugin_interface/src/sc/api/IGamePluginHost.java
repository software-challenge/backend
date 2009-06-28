package sc.api;

public interface IGamePluginHost {
	public void registerProtocolClass(Class<?> clazz);
	public void registerProtocolClass(Class<?> clazz, String alias);
}
