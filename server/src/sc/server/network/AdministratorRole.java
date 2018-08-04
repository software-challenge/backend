package sc.server.network;

/** Client Role with all rights */
public class AdministratorRole implements IClientRole {
  private Client client;

  public AdministratorRole(Client client) {
    this.client = client;
  }

  /** Getter for the Client */
  @Override
  public Client getClient() {
    return this.client;
  }

  /** Close the Administrator */
  @Override
  public void close() {
  }

}
