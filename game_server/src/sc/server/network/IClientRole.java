package sc.server.network;

public interface IClientRole
{
	public IClient getClient();

	public void close();
}
