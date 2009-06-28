package sc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceManager
{
	private static Logger		logger	= LoggerFactory
												.getLogger(ServiceManager.class);

	private static Set<Thread>	threads	= new HashSet<Thread>();

	public static Thread createService(Runnable target)
	{
		return createService(target, true);
	}

	public static Thread createService(Runnable target, boolean daemon)
	{
		logger.debug("Spawning Thread for new service (daemon = {})", daemon);

		Thread thread = new Thread(target);
		thread.setDaemon(daemon);
		threads.add(thread);
		return thread;
	}

	public static void killAll()
	{
		logger.info("Shutting down all services...");
		
		for (Thread thread : threads)
		{
			thread.interrupt();
		}
	}

	public static Collection<Thread> getAll()
	{
		return Collections.unmodifiableCollection(threads);
	}
}
