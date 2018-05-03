package sc.server.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NonEndingByteArrayInputStream extends InputStream
{
	private Object					hold = new Object();
	private ByteArrayInputStream	byteArray;

	public NonEndingByteArrayInputStream(byte[] buf)
	{
		this.byteArray = new ByteArrayInputStream(buf);
	}

	private void waitForever() throws IOException
	{
		synchronized (this.hold)
		{
			try
			{
				this.hold.wait();
			}
			catch (InterruptedException e)
			{
				throw new IOException(e);
			}
		}
	}

	public synchronized int read(byte b[], int off, int len) throws IOException
	{
		int result = this.byteArray.read(b, off, len);
		if(result < 0)
		{
			waitForever();
		}
		return result;
	}
	
	@Override
	public synchronized void mark(int readlimit)
	{
		this.byteArray.mark(readlimit);
	}
	
	@Override
	public void close() throws IOException
	{
		this.byteArray.close();
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		return this.byteArray.skip(n);
	}
	
	@Override
	public boolean markSupported()
	{
		return this.byteArray.markSupported();
	}
	
	@Override
	public int read(byte[] b) throws IOException
	{
		int result = this.byteArray.read(b);
		if(result < 0)
		{
			waitForever();
		}
		return result;
	}
	
	@Override
	public synchronized void reset() throws IOException
	{
		this.byteArray.reset();
	}
	

	@Override
	public int available() throws IOException
	{
		return Math.max(0, this.byteArray.available());
	}

	@Override
	public int read() throws IOException
	{
		int result = this.byteArray.read();
		if (result < 0)
		{
			waitForever();
		}
		return result;
	}

}
