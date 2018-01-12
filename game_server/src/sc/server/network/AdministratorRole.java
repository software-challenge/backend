package sc.server.network;

/**
 * Client Role with all rights
 */
public class AdministratorRole implements IClientRole
{
  /* private fields */
  private Client	client;

  /* constructor */
  public AdministratorRole(Client client)
  {
    this.client = client;
  }

  /* methods */

  /**
   * Getter for the Client
   * @return
   */
  @Override
  public Client getClient()
  {
    return this.client;
  }

  /**
   * Close the Administrator
   */
  @Override
  public void close()
  {
    // TODO Auto-generated method stub
  }
}
