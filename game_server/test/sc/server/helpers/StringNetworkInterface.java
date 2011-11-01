package sc.server.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sc.networking.INetworkInterface;


public class StringNetworkInterface implements INetworkInterface
{
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private InputStream inputStream;
		
	public StringNetworkInterface(String data)
	{
		this.inputStream = new NonEndingByteArrayInputStream(data.getBytes());
	}
	
	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return this.inputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return this.outputStream;
	}
	
	public String getData() throws IOException
	{
		this.outputStream.flush();
		return this.outputStream.toString();
	}
	
	@Override
	public String toString()
	{
		return "String@" + this.hashCode();
	}
}
