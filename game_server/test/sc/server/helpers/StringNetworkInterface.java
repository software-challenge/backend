package sc.server.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sc.server.network.interfaces.INetworkInterface;


public class StringNetworkInterface implements INetworkInterface
{
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private ByteArrayInputStream inputStream;
		
	public StringNetworkInterface(String data)
	{
		inputStream = new ByteArrayInputStream(data.getBytes());
	}
	
	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return outputStream;
	}
	
	public String getData() throws IOException
	{
		outputStream.flush();
		return outputStream.toString();
	}
	
	@Override
	public String toString()
	{
		return "String@" + this.hashCode();
	}
}
