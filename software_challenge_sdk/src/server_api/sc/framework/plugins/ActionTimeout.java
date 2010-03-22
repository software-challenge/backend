package sc.framework.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionTimeout
{
	static final Logger			logger					= LoggerFactory
																.getLogger(ActionTimeout.class);

	private final long			softTimeoutInMilliseconds;

	private final long			hardTimeoutInMilliseconds;

	private final boolean		canTimeout;

	private Thread				timeoutThread;

	private Status				status					= Status.NEW;

	private long				startTimestamp			= 0;
	private long				stopTimestamp			= 0;

	private static final long	DEFAULT_HARD_TIMEOUT	= 10000;
	private static final long	DEFAULT_SOFT_TIMEOUT	= 5000;

	private enum Status
	{
		NEW, STARTED, STOPPED
	}

	public ActionTimeout(boolean canTimeout)
	{
		this(canTimeout, DEFAULT_HARD_TIMEOUT, DEFAULT_SOFT_TIMEOUT);
	}

	public ActionTimeout(boolean canTimeout, long hardTimeoutInMilliseconds)
	{
		this(canTimeout, hardTimeoutInMilliseconds, hardTimeoutInMilliseconds);
	}

	public ActionTimeout(boolean canTimeout, long hardTimeoutInMilliseconds, long softTimeoutInMilliseconds)
	{
		if (hardTimeoutInMilliseconds < softTimeoutInMilliseconds)
		{
			throw new IllegalArgumentException(
					"HardTimeout must be greater or equal the SoftTimeout");
		}

		this.canTimeout = canTimeout;
		this.softTimeoutInMilliseconds = softTimeoutInMilliseconds;
		this.hardTimeoutInMilliseconds = hardTimeoutInMilliseconds;
	}

	public long getHardTimeout()
	{
		return this.hardTimeoutInMilliseconds;
	}

	public long getSoftTimeout()
	{
		return this.softTimeoutInMilliseconds;
	}

	public boolean canTimeout()
	{
		return this.canTimeout;
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
			logger.info "Time needed: " + (this.stopTimestamp - this.startTimestamp);
			return (this.stopTimestamp - this.startTimestamp) > this.softTimeoutInMilliseconds;
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

		if (this.timeoutThread != null)
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
