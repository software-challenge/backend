package sc.server.network.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpNetwork implements INetworkInterface
{
	private Socket	socket;

	public TcpNetwork(Socket socket)
	{
		this.socket = socket;
	}
	
	/* (non-Javadoc)
	 * @see edu.cau.sc.server.network.interfaces.INetworkInterface#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return this.socket.getInputStream();
	}
	
	/* (non-Javadoc)
	 * @see edu.cau.sc.server.network.interfaces.INetworkInterface#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return this.socket.getOutputStream();
	}
	
	/* (non-Javadoc)
	 * @see edu.cau.sc.server.network.interfaces.INetworkInterface#close()
	 */
	public void close() throws IOException
	{
		this.socket.close();
	}
}
