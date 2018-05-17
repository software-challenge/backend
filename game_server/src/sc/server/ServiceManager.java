package sc.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages all threads
 */

public abstract class ServiceManager
{
	private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);

	private static Set<Thread>	threads = new HashSet<Thread>();
	private static Set<Thread>	killedThreads = new HashSet<Thread>();

	/**
	 * Creates a new Thread
	 * @param name Name of new Thread
	 * @param target instance of Runnable
	 * @return
	 */
	public static Thread createService(String name, Runnable target)
	{
		return createService(name, target, true);
	}

	private static synchronized Thread createService(String name,
			Runnable target, boolean daemon)
	{
		logger.debug("Spawning thread for new service (name={}, daemon={})",
				name, daemon);

		Thread thread = new Thread(target);
		thread.setName(name);
		thread.setDaemon(daemon);
		threads.add(thread);
		return thread;
	}

	private static synchronized void kill(Thread thread)
	{
		thread.interrupt();
		threads.remove(thread);
		killedThreads.add(thread);
	}

	public static synchronized void killAll()
	{
		logger.info("Shutting down all services...");

		List<Thread> clonedList = new ArrayList<Thread>(threads.size());
		clonedList.addAll(threads);

		for (Thread thread : clonedList)
		{
			kill(thread);
		}
	}

}
