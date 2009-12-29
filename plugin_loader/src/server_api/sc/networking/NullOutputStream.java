package sc.networking;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullOutputStream extends OutputStream
{
	private boolean				closed		= false;
	private boolean				warnOnWrite	= false;
	private static final Logger	logger		= LoggerFactory
													.getLogger(NullOutputStream.class);

	public NullOutputStream()
	{
		this(false);
	}

	public NullOutputStream(boolean warnOnWrite)
	{
		this.warnOnWrite = warnOnWrite;
	}

	@Override
	public void write(int b) throws IOException
	{
		warn();
		if (this.closed)
			throw new IOException("Write to closed stream");
	}

	private void warn()
	{
		if (this.warnOnWrite)
		{
			logger.warn("Wrote data to a NullOutputStream.");
		}
	}

	@Override
	public void write(byte[] data, int offset, int length) throws IOException
	{
		warn();

		if (data == null)
			throw new NullPointerException("data is null");
		if (this.closed)
			throw new IOException("Write to closed stream");
	}

	@Override
	public void close()
	{
		this.closed = true;
	}
}
