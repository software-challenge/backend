package sc.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpNetwork implements INetworkInterface {
  private static Logger logger = LoggerFactory.getLogger(TcpNetwork.class);
  private Socket socket;

  /**
   * Constructs an implementation of <code>INetworkInterface</code> which
   * operates on Java's <code>Socket</code>.
   *
   * @param socket Socket to use for construction
   *
   * @throws IOException thrown if socket is invalid
   */
  public TcpNetwork(Socket socket) throws IOException {
    this.socket = socket;
    this.socket.setTcpNoDelay(true);
  }

  // @see edu.cau.sc.server.network.interfaces.INetworkInterface#getInputStream()
  @Override
  public InputStream getInputStream() throws IOException {
    return this.socket.getInputStream();
  }

  // @see edu.cau.sc.server.network.interfaces.INetworkInterface#getOutputStream()
  @Override
  public OutputStream getOutputStream() throws IOException {
    return this.socket.getOutputStream();
  }

  // @see edu.cau.sc.server.network.interfaces.INetworkInterface#close()
  @Override
  public void close() throws IOException {
    logger.debug("Closing TcpNetwork Interface.");
    this.socket.close();
  }

  @Override
  public String toString() {
    return "TcpNetwork{socket=" + socket + "}";
  }

}
