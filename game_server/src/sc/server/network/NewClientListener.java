package sc.server.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.TcpNetwork;
import sc.server.Configuration;
import sc.server.ServiceManager;

/**
 * Listener, which waits for new clients
 */
public class NewClientListener implements Runnable, Closeable	{


	/* private fields */
  private ServerSocket				serverSocket;
  private Thread						thread;

  /* final fields */
  private final BlockingQueue<Client>	queue;


  /* static fields */
  protected static final Logger		logger			= LoggerFactory
          .getLogger(NewClientListener.class);
  public static int					lastUsedPort	= 0;

  /* constructor */
  NewClientListener(){
    this.serverSocket = null;
    this.thread = null;
    this.queue = new LinkedBlockingQueue<>();
  }

  /**
   * Returns a new connected client, if a new one is available. Otherwise this
   * method blocks until a new client connects.
   *
   * @return
   * @throws InterruptedException
   *             If interrupted while waiting for a new client.
   */
  public Client fetchNewSingleClient() throws InterruptedException
  {
    return this.queue.take();
  }

  /**
   * Accept clients in blocking mode
   */
  private void acceptClient()
  {
    try
    {
      Socket clientSocket = this.serverSocket.accept();

      logger.info("A Client connected...");

      Client newClient = new Client(new TcpNetwork(clientSocket),
              Configuration.getXStream());

      try
      {
        this.queue.put(newClient);
        logger.info("Added Client " + newClient + " to ReadyQueue.");
      }
      catch (InterruptedException e)
      {
        logger.error("Client could not be added to ready queue.", e);
      }
    }
    catch (IOException e)
    {
      if (this.serverSocket.isClosed())
      {
        logger.warn("ServerSocket has been closed.");
      }
      else
      {
        logger.error("Unexpected exception occurred {}", e);
      }
    }
  }

  /**
   * infinite loop to wait asynchronously for clients
   */
  @Override
  public void run()
  {
    while (!this.serverSocket.isClosed() && !Thread.interrupted())
    {
      acceptClient();
      Thread.yield();
    }
  }

  /**
   * Start the listener and create a daemon thread from this object
   * @throws IOException
   */
  public void start() throws IOException
  {
    startSocketListener();
    if (this.thread == null) {
      this.thread = ServiceManager.createService(this.getClass().getSimpleName(), this);
      this.thread.start();
    }
  }

  /**
   * Start the listener, whilst opening a port
   * @throws IOException if server could not be started on set port
   */
  private void startSocketListener() throws IOException
  {
    int port = Configuration.getPort();
    InetAddress bindAddr = null; // From the docs: If bindAddr is null, it will default accepting connections on any/all local addresses.
    if (Configuration.getListenLocal()) {
      bindAddr = InetAddress.getByName(null); // localhost
    }

    try
    {
      this.serverSocket = new ServerSocket(port, 0, bindAddr);
      int usedPort = this.serverSocket.getLocalPort();
      NewClientListener.lastUsedPort = usedPort;
      logger.info("Listening on port {} for incoming connections.",
              usedPort);
    }
    catch (IOException e)
    {
      logger.error("Could not start server on port " + port, e);
      throw e;
      // do not throw a new IOException to preserve the inheritance hierarchy
    }
  }

  /**
   * close the socket
   */
  @Override
  public void close()
  {
    try
    {
      logger.info("Shutting down NewClientListener...");

      if (this.serverSocket != null)
      {
        this.serverSocket.close();
      }
    }
    catch (IOException e)
    {
      logger.warn("Couldn't close socket.", e);
      if (this.thread != null) {
        this.thread.interrupt();
      }
    }
  }
}
