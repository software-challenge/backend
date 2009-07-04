package sc.api.plugins;

public interface IGamePlugin extends IPlugin<IGamePluginHost>
{
	public IGameInstance createGame();

	public int getMaximumPlayerCount();
}
