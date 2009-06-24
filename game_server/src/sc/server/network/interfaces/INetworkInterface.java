package sc.server.network.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface INetworkInterface
{

	public abstract InputStream getInputStream() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

	public abstract void close() throws IOException;

}
