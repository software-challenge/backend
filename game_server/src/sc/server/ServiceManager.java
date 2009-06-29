package sc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.helpers.CollectionHelper;
import sc.helpers.Function;
import sc.helpers.StringHelper;

public abstract class ServiceManager
{
	private static Logger		logger			= LoggerFactory
														.getLogger(ServiceManager.class);

	private static Set<Thread>	threads			= new HashSet<Thread>();
	private static Set<Thread>	killedThreads	= new HashSet<Thread>();

	static
	{
		createService("ServiceMonitor", new Runnable() {

			@Override
			public void run()
			{
				Function<Thread, String> mapper = new Function<Thread, String>() {

					@Override
					public String operate(Thread val)
					{
						return val.getName();
					}

				};

				while (!Thread.interrupted())
				{
					logger.info("Active Services: ({})", StringHelper.join(
							CollectionHelper.map(getServices(), mapper), ", "));

					try
					{
						Thread.sleep(60000);
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
			}

		}).start();
	}

	public static Thread createService(String name, Runnable target)
	{
		return createService(name, target, true);
	}

	protected static Collection<Thread> getServices()
	{
		return Collections.unmodifiableCollection(threads);
	}

	protected static Collection<Thread> getServiceNames()
	{
		return Collections.unmodifiableCollection(threads);
	}

	public static Thread createService(String name, Runnable target,
			boolean daemon)
	{
		logger.debug("Spawning Thread for new service (name={}, daemon={})",
				name, daemon);

		Thread thread = new Thread(target);
		thread.setName(name);
		thread.setDaemon(daemon);
		threads.add(thread);
		return thread;
	}

	private static void kill(Thread thread)
	{
		thread.interrupt();
		threads.remove(thread);
		killedThreads.add(thread);
	}

	public static void killAll()
	{
		logger.info("Shutting down all services...");

		for (Thread thread : threads)
		{
			kill(thread);
		}
	}

	public static Collection<Thread> getAll()
	{
		return Collections.unmodifiableCollection(threads);
	}

	public static boolean isEmpty()
	{
		return threads.isEmpty();
	}
}
