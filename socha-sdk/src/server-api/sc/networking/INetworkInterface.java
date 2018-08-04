package sc.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for the "Network"-component. By using this interface it is much
 * easier to write tests (with Mock-objects) so that you don't need a working
 * TCP/IP client.
 */
public interface INetworkInterface {

  InputStream getInputStream() throws IOException;

  OutputStream getOutputStream() throws IOException;

  void close() throws IOException;

}
