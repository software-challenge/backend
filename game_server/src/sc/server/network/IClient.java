package sc.server.network;


public interface IClient
{
	void addRole(IClientRole role);

	void send(Object toSend);

	void sendAsynchronous(Object packet);
}
