package sc.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for the "Network"-component. By using this interface it is much
 * easier to write tests (with Mock-objects) so that you don't need a working
 * TCP/IP client.
 */
public interface INetworkInterface
{

	public abstract InputStream getInputStream() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

	public abstract void close() throws IOException;

}
