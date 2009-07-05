package sc.api.plugins;

import java.util.Collection;

public interface IGamePluginHost
{
	public void registerProtocolClass(Class<?> clazz);

	public void registerProtocolClass(Class<?> clazz, String alias);

	public void registerProtocolClasses(
			Collection<Class<? extends Object>> protocolClasses);
}
