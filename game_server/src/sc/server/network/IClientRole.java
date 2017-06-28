package sc.server.network;

/**
 * Interface for each Role there is
 */
public interface IClientRole
{
  /**
   * Getter for the client
   * @return the client
   */
	public IClient getClient();

  /**
   * Close the role.
   */
	public void close();
}
