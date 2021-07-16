package sc.server.network;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.networking.INetworkInterface;
import sc.networking.UnprocessedPacketException;
import sc.networking.clients.IClient;
import sc.networking.clients.XStreamClient;
import sc.protocol.ProtocolPacket;
import sc.protocol.responses.ErrorPacket;
import sc.server.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles server-side communication with connected clients.
 *
 * Clients which connect to the server (as separate programs or running as threads started by the server)
 * are represented by {@link sc.networking.clients.LobbyClient}.
 */
public class Client extends XStreamClient implements IClient {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  protected boolean isAdministrator = false;
  private boolean notifiedOnDisconnect = false;
  private IClientRequestListener requestHandler = null;
  private final List<IClientListener> clientListeners = new ArrayList<>();

  public Client(INetworkInterface networkInterface) throws IOException {
    super(networkInterface);
  }

  public void setRequestHandler(IClientRequestListener handler) {
    requestHandler = handler;
  }

  /** Add a {@link IClientListener listener} to the client. */
  public void addClientListener(IClientListener listener) {
    this.clientListeners.add(listener);
  }

  public void removeClientListener(IClientListener listener) {
    this.clientListeners.remove(listener);
  }

  /**
   * Test if this client is an administrator.
   *
   * @return true iff this client has an AdministratorRole
   */
  public boolean isAdministrator() {
    return isAdministrator;
  }

  /**
   * Authenticates a Client as Administrator
   *
   * @param password The secret that is required to gain administrative rights.
   */
  public void authenticate(String password) throws AuthenticationFailedException {
    String correctPassword = Configuration.getAdministrativePassword();

    if (correctPassword != null && correctPassword.equals(password)) {
      if (!isAdministrator()) {
        isAdministrator = true;
        logger.info("Client authenticated as administrator");
      } else {
        logger.warn("Client tried to authenticate as administrator twice.");
      }
    } else {
      logger.warn("Client failed to authenticate as administrator.");

      throw new AuthenticationFailedException();
    }
  }

  /** Disconnect the client and cleanup. */
  @Override
  protected void onDisconnect(DisconnectCause cause) {
    if (!this.notifiedOnDisconnect) {
      this.notifiedOnDisconnect = true;
      for (IClientListener listener : new ArrayList<>(clientListeners)) {
        try {
          listener.onClientDisconnected(this, cause);
        } catch (Exception e) {
          logger.error("OnDisconnect Notification caused an exception.", e);
        }
      }
    }
  }

  /** Forward received package to listeners. */
  @Override
  protected void onObject(@NotNull ProtocolPacket message) throws UnprocessedPacketException {
    /*
     * NOTE that this method is called in the receiver thread.
     * Messages should only be passed to listeners.
     * No callbacks should be invoked directly in the receiver thread.
     */
    Collection<RescuableClientException> errors = new ArrayList<>();

    PacketCallback callback = new PacketCallback(message);

    try {
      requestHandler.onRequest(this, callback);
    } catch (RescuableClientException e) {
      errors.add(e);
    }

    if (errors.isEmpty() && !callback.isProcessed()) {
      String msg = String.format("Packet %s wasn't processed.", message);
      logger.warn(msg);
      throw new UnprocessedPacketException(msg);
    }

    for (RescuableClientException error : errors) {
      logger.warn("Error on " + message, error);
      send(new ErrorPacket(message, error.toString()));
    }
  }

}
