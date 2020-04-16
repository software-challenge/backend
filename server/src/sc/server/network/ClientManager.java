package sc.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.server.Lobby;
import sc.server.ServiceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** The ClientManager serves as a lookup table for all active connections. */
public class ClientManager implements Runnable, IClientListener {
  private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

  // Lobby which we are connected to
  private Lobby lobby;
  private boolean running;
  private Thread thread;

  // List of all XStreamClients
  protected final List<Client> clients;

  // Listener waits for new clients to connect
  private final NewClientListener clientListener;

  /**
   * Create manager from {@link Lobby lobby}
   *
   * @param lobby from which the manager is created
   */
  public ClientManager(Lobby lobby) {
    this.clientListener = new NewClientListener();
    this.lobby = lobby;
    this.clients = new ArrayList<>();
    this.running = false;
    this.thread = null;
  }

  /**
   * Adds the given <code>newClient</code> and notifies all listeners by
   * invoking <code>onClientConnected</code>.<br>
   * <i>(only used by tests and addAll())</i>
   */
  public void add(Client newClient) {
    this.clients.add(newClient);
    newClient.addClientListener(this);
    this.lobby.onClientConnected(newClient);
  }

  /** Used for testing */
  public List<Client> getClients() {
    return this.clients;
  }

  /** Fetch new clients */
  @Override
  public void run() {
    this.running = true;

    logger.info("ClientManager running.");

    while (this.running && !Thread.interrupted()) {
      try {
        // Waits blocking for new Client
        Client client = this.clientListener.fetchNewSingleClient();

        logger.info("Delegating new client to ClientManager...");
        this.add(client);
        logger.info("Delegation done.");
      } catch (InterruptedException e) {
        if (this.running) {
          logger.error("Interrupted while waiting for a new client.", e);
        } else {
          logger.error("Client manager is shutting down");
        }
        // TODO should it be handled?
      }

    }

    this.running = false;
    logger.info("ClientManager closed.");
  }

  /**
   * Starts the ClientManager in it's own daemon thread. This method should be used only once.
   * clientListener starts SocketListener on defined port to watch for new connecting clients
   */
  public void start() throws IOException {
    this.clientListener.start();
    if (this.thread == null) {
      this.thread = ServiceManager.createService(this.getClass().getSimpleName(), this);
      this.thread.start();
    }
  }

  /**
   * Set the {@link Lobby lobby}.
   *
   * @param lobby to be set
   */
  public void setLobby(Lobby lobby) {
    this.lobby = lobby;
  }

  public void close() {
    this.running = false;

    if (this.thread != null) {
      this.thread.interrupt();
    }

    this.clientListener.close();

    for (int i = 0; i < this.clients.size(); i++) {
      Client client = this.clients.get(i);
      client.stop();
    }
  }

  /**
   * On client disconnect remove it from the list
   *
   * @param source client which disconnected
   */
  @Override
  public void onClientDisconnected(Client source) {
    logger.info("Removing client {} from client manager", source);
    clients.remove(source);
  }

  /**
   * Do nothing on error
   *
   * @param source client, which rose the error
   * @param packet which contains the error
   */
  @Override
  public void onError(Client source, ProtocolErrorMessage packet) {
    // TODO Error handling needs to happen
  }

  /**
   * Ignore any request
   *
   * @param source client, which send the package
   * @param packet to be handled
   *
   * @throws RescuableClientException never
   */
  @Override
  public void onRequest(Client source, PacketCallback packet)
          throws RescuableClientException {
    // TODO Handle Request?
  }

}
