package sc.server.network.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpNetwork implements INetworkInterface
{
	private static Logger	logger	= LoggerFactory.getLogger(TcpNetwork.class);
	private Socket			socket;

	public TcpNetwork(Socket socket) throws IOException
	{
		this.socket = socket;
		this.socket.setTcpNoDelay(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.cau.sc.server.network.interfaces.INetworkInterface#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return this.socket.getInputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.cau.sc.server.network.interfaces.INetworkInterface#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return this.socket.getOutputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cau.sc.server.network.interfaces.INetworkInterface#close()
	 */
	public void close() throws IOException
	{
		logger.debug("Closed TcpNetwork Interface.");
		this.socket.close();
	}
}
