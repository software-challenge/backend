package sc.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.TcpNetwork;
import sc.server.Configuration;
import sc.server.ServiceManager;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** Listener that waits for new clients and returns ready TCP connections to them. */
public class NewClientListener implements Runnable, Closeable {

  private ServerSocket serverSocket;
  private Thread thread;

  private final BlockingQueue<Client> queue;

  protected static final Logger logger = LoggerFactory.getLogger(NewClientListener.class);
  public static int lastUsedPort = 0;

  NewClientListener() {
    this.serverSocket = null;
    this.thread = null;
    this.queue = new LinkedBlockingQueue<>();
  }

  /**
   * Returns a new connected client, if a new one is available.
   * Otherwise this method blocks until a new client connects.
   *
   * @throws InterruptedException If interrupted while waiting for a new client.
   */
  public Client fetchNewSingleClient() throws InterruptedException {
    return this.queue.take();
  }

  /** Wait for a client to connect and add it to the queue. */
  private void acceptClient() {
    try {
      Socket clientSocket = this.serverSocket.accept();

      logger.info("A Client connected...");

      Client newClient = new Client(new TcpNetwork(clientSocket));

      try {
        this.queue.put(newClient);
        logger.info("Added Client " + newClient + " to ReadyQueue.");
      } catch (InterruptedException e) {
        logger.error("Client could not be added to ready queue.", e);
      }
    } catch (IOException e) {
      if (this.serverSocket.isClosed()) {
        logger.warn("ServerSocket has been closed.");
      } else {
        logger.error("Unexpected exception occurred {}", e);
      }
    }
  }

  /** Infinite loop to wait asynchronously for clients. */
  @Override
  public void run() {
    while (!this.serverSocket.isClosed() && !Thread.interrupted()) {
      acceptClient();
      Thread.yield();
    }
  }

  /**
   * Start the listener and create a daemon thread from this object.
   *
   * The SocketListener then watches the {@link Configuration#getPort()} for new connecting clients.
   */
  public void start() throws IOException {
    startSocketListener();
    if (this.thread == null) {
      this.thread = ServiceManager.createService(this.getClass().getSimpleName(), this);
      this.thread.start();
    }
  }

  /**
   * Start listening on the configured port.
   *
   * @throws IOException if server could not be started on set port
   */
  private void startSocketListener() throws IOException {
    int port = Configuration.getPort();
    InetAddress bindAddr = null; // From the docs: If bindAddr is null, it will default accepting connections on any/all local addresses.
    if (Configuration.getListenLocal()) {
      bindAddr = InetAddress.getByName(null); // localhost
    }

    try {
      this.serverSocket = new ServerSocket(port, 0, bindAddr);
      int usedPort = this.serverSocket.getLocalPort();
      NewClientListener.lastUsedPort = usedPort;
      logger.info("Listening on port {} for incoming connections.",
              usedPort);
    } catch (IOException e) {
      logger.error("Could not start server on port " + port, e);
      throw e;
      // do not throw a new IOException to preserve the inheritance hierarchy
    }
  }

  /** Close the socket. */
  @Override
  public void close() {
    try {
      logger.info("Shutting down NewClientListener...");

      if (this.serverSocket != null) {
        this.serverSocket.close();
      }
    } catch (IOException e) {
      logger.warn("Couldn't close socket.", e);
      if (this.thread != null) {
        this.thread.interrupt();
      }
    }
  }

}
