package sc.framework.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionTimeout
{
	static final Logger		logger			= LoggerFactory
													.getLogger(ActionTimeout.class);

	private final long		softTimeout;

	private final long		hardTimeout;

	private final boolean	timeout;

	private Thread			timeoutThread;

	private Status			status			= Status.NEW;

	private long			startTimestamp	= 0;
	private long			stopTimestamp	= 0;

	private enum Status
	{
		NEW, STARTED, STOPPED
	}

	public ActionTimeout(boolean timeout)
	{
		this(timeout, 10000, 5000);
	}

	public ActionTimeout(boolean timeout, long hardTimeout)
	{
		this(timeout, hardTimeout, hardTimeout);
	}

	public ActionTimeout(boolean timeout, long hardTimeout, long softTimeout)
	{
		if (hardTimeout < softTimeout)
		{
			throw new IllegalArgumentException(
					"HardTimeout must be greater or equal the SoftTimeout");
		}

		this.timeout = timeout;
		this.softTimeout = softTimeout;
		this.hardTimeout = hardTimeout;
	}

	public long getHardTimeout()
	{
		return this.hardTimeout;
	}

	public long getSoftTimeout()
	{
		return this.softTimeout;
	}

	public boolean canTimeout()
	{
		return this.timeout;
	}

	public synchronized boolean didTimeout()
	{
		if (this.status == Status.NEW)
		{
			throw new IllegalStateException("Timeout wasn't started.");
		}

		if (this.status == Status.STARTED)
		{
			throw new IllegalStateException("Timeout wasn't stopped.");
		}

		if (this.canTimeout())
		{
			return (this.stopTimestamp - this.startTimestamp) > this.softTimeout;
		}

		return false;
	}

	public synchronized void stop()
	{
		if (this.status == Status.NEW)
		{
			throw new IllegalStateException("Timeout was never started.");
		}

		if (this.status == Status.STOPPED)
		{
			logger.warn("Timeout was already stopped.");
			return;
		}

		this.stopTimestamp = System.currentTimeMillis();
		this.status = Status.STOPPED;
		
		if(this.timeoutThread != null)
		{
			this.timeoutThread.interrupt();			
		}
	}

	public synchronized void start(final Runnable onTimeout)
	{
		if (this.status != Status.NEW)
		{
			throw new IllegalStateException("Was already started!");
		}

		if (canTimeout())
		{
			this.timeoutThread = new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(getHardTimeout());
						stop();
						onTimeout.run();
					}
					catch (InterruptedException e)
					{
						logger.info("HardTimout wasn't reached.");
					}
				}
			});
			this.timeoutThread.start();
		}

		this.startTimestamp = System.currentTimeMillis();
		this.status = Status.STARTED;
	}
}
