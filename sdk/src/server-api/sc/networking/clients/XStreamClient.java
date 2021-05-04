package sc.networking.clients;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.networking.UnprocessedPacketException;
import sc.networking.XStreamProvider;
import sc.protocol.CloseConnection;
import sc.protocol.ProtocolPacket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public abstract class XStreamClient {
  private static Logger logger = LoggerFactory.getLogger(XStreamClient.class);

  public static INetworkInterface createTcpNetwork(String host, int port) throws IOException {
    logger.info("Creating TCP Network for {}:{}", host, port);
    return new TcpNetwork(new Socket(host, port));
  }

  private final INetworkInterface networkInterface;
  private final ObjectOutputStream out;
  private final Thread receiveThread;
  protected final XStream xStream = XStreamProvider.loadPluginXStream();

  private DisconnectCause disconnectCause = DisconnectCause.NOT_DISCONNECTED;
  private boolean closed = false;
  private boolean ready = false;
  private final Object readyLock = new Object();

  public enum DisconnectCause {
    /** default state */
    NOT_DISCONNECTED,
    /** disconnected because CloseConnection was received (disconnected by other side) */
    RECEIVED_DISCONNECT,
    /** disconnected from this side */
    DISCONNECTED,
    // error conditions
    PROTOCOL_ERROR,
    LOST_CONNECTION,
    TIMEOUT,
    UNKNOWN
  }

  public boolean isReady() {
    return ready;
  }

  /** Signals that client can receive and send. */
  public void start() {
    synchronized(readyLock) {
      if (!ready) {
        ready = true;
        readyLock.notifyAll();
      }
    }
  }

  public XStreamClient(final INetworkInterface networkInterface) throws IOException {
    if (networkInterface == null)
      throw new IllegalArgumentException("networkInterface must not be null.");

    this.networkInterface = networkInterface;
    this.out = xStream.createObjectOutputStream(networkInterface.getOutputStream(), "protocol");
    this.receiveThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          receiveThread();
        } catch (Exception e) {
          logger.error("ReceiveThread caused an exception.", e);
        }
        logger.debug("Terminated {}", receiveThread.getName());
      }
    });
    this.receiveThread.setName(String.format("XStream-Receive id:%d of %s", receiveThread.getId(), shortString()));
    this.receiveThread.start();
  }

  protected abstract void onObject(@NotNull ProtocolPacket message) throws UnprocessedPacketException;

  /** Used by the receiving thread. All exceptions should be handled. */
  public void receiveThread() {
    try(ObjectInputStream in = xStream.createObjectInputStream(networkInterface.getInputStream())) {
      synchronized(readyLock) {
        while (!isReady()) {
          readyLock.wait();
        }
      }

      while (!Thread.interrupted()) {
        Object object = in.readObject();
        if (object instanceof ProtocolPacket) {
          ProtocolPacket response = (ProtocolPacket) object;

          logger.debug("{}: Received {} via {}", shortString(), response, networkInterface);
          if (logger.isTraceEnabled())
            logger.trace("Dumping {}:\n{}", response, xStream.toXML(response));

          if (response instanceof CloseConnection) {
            handleDisconnect(DisconnectCause.RECEIVED_DISCONNECT);
            // handleDisconnect takes care of stopping the thread
          } else {
            onObject(response);
          }
        } else {
          throw new ClassNotFoundException("Received object of unknown class " + object.getClass().getName());
        }
      }
    } catch (IOException e) {
      // The other side closed the connection. It is better when the other
      // side sends a CloseConnection message before, giving this side the
      // chance to close the connection regularly.
      // NOTE that a XStreamClient exists on both sides of the connection
      // (as a Client object on the server side and as a LobbyClient
      // object on the client side).
      handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
    } catch (ClassNotFoundException e) {
      handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
    } catch (XStreamException e) {
      Throwable exceptionCause = e.getCause();
      if (exceptionCause != null) {
        if (exceptionCause instanceof SocketException) {
          // If the thread was interrupted, we have a regular disconnect.
          // Unfortunately, OIS.readObject() doesn't react to interruptions directly.
          if(!Thread.interrupted())
            handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
        } else if (exceptionCause instanceof EOFException) {
          handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
        } else if (exceptionCause instanceof IOException
                && exceptionCause.getCause() != null && exceptionCause
                .getCause() instanceof InterruptedException) {
          handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
        } else {
          handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
        }
      } else {
        handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
      }
    } catch (Exception e) {
      logger.error("Unknown Communication Error", e);
      handleDisconnect(DisconnectCause.UNKNOWN, e);
    }
  }

  public void sendCustomData(String data) throws IOException {
    logger.debug("{}: Sending custom data: {}", shortString(), data);
    sendCustomData(data.getBytes(StandardCharsets.UTF_8));
  }

  public void sendCustomData(byte[] data) throws IOException {
    logger.info("{}: Sending custom data (size={})", shortString(), data.length);
    networkInterface.getOutputStream().write(data);
    networkInterface.getOutputStream().flush();
  }

  public synchronized void send(ProtocolPacket packet) {
    if (!isReady())
      throw new IllegalStateException(
          String.format("Trying to write packet on %s which wasn't started: %s", shortString(), packet));

    if (isClosed()) {
      logger.warn("Writing on a closed Stream -> dropped the packet (tried to send package of type {}) Thread: {}",
          packet.getClass().getSimpleName(),
          Thread.currentThread().getName());
      return;
    }

    logger.debug("{}: Sending {} via {} from {}", shortString(), packet, networkInterface, toString());
    if (logger.isTraceEnabled())
      logger.trace("Dumping {}:\n{}", packet, xStream.toXML(packet));

    try {
      this.out.writeObject(packet);
      this.out.flush();
    } catch (XStreamException e) {
      handleDisconnect(DisconnectCause.PROTOCOL_ERROR, e);
    } catch (IOException e) {
      handleDisconnect(DisconnectCause.LOST_CONNECTION, e);
    }
  }

  protected final void handleDisconnect(DisconnectCause cause) {
    handleDisconnect(cause, null);
  }

  protected final void handleDisconnect(DisconnectCause cause, Throwable exception) {
    if (exception != null) {
      logger.warn("{} disconnected (Cause: {}, Exception: {})", shortString(), cause, exception);
      if (logger.isDebugEnabled())
        exception.printStackTrace();
    } else {
      logger.info("{} disconnected (Cause: {})", shortString(), cause);
    }

    this.disconnectCause = cause;

    try {
      close();
    } catch (Exception e) {
      logger.error("Failed to close", e);
    }

    onDisconnect(cause);
  }

  protected void onDisconnect(DisconnectCause cause) {
  }

  public DisconnectCause getDisconnectCause() {
    return this.disconnectCause;
  }

  /**
   * Should be called when the client needs to be stopped and the disconnect
   * is initiated on this side. There are two situations where this should be
   * done:
   * <p>
   * - A game has ended
   * - An internal error occurred (this situation might be redundant)
   */
  public void stop() {
    // this side caused disconnect, notify other side
    send(new CloseConnection());
    handleDisconnect(DisconnectCause.DISCONNECTED);
  }

  protected synchronized void stopReceiver() {
    logger.info("Stopping {}", receiveThread.getName());
    if (this.receiveThread.getId() == Thread.currentThread().getId()) {
      logger.warn("ReceiveThread is stopping itself");
    }
    // unlock waiting threads
    synchronized(this.readyLock) {
      this.readyLock.notifyAll();
    }
    this.receiveThread.interrupt();
  }

  protected synchronized void close() {
    if (!isClosed()) {
      this.closed = true;

      stopReceiver();

      try {
        if (this.out != null)
          this.out.close();
      } catch (Exception e) {
        if (e.getCause() instanceof SocketException)
          logger.debug("Failed to close OUT", e);
        else
          logger.warn("Failed to close OUT", e);
      }

      try {
        this.networkInterface.close();
      } catch (Exception e) {
        logger.warn("Failed to close " + networkInterface, e);
      }
    } else {
      logger.warn("Attempted to close an already closed stream");
    }
  }

  public XStream getXStream() {
    return this.xStream;
  }

  public boolean isClosed() {
    return this.closed;
  }

  public String shortString() {
    return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
  }

  @Override
  public String toString() {
    return shortString();
  }
}
