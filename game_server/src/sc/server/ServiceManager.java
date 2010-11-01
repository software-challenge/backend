package sc.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

	protected static synchronized Collection<Thread> getServices()
	{
		LinkedList<Thread> threadsToKill = new LinkedList<Thread>();
		for (Thread thread : threads) {
			try {
				if(!thread.isAlive()) {
					threadsToKill.add(thread);
				}
			} catch (Exception e) {
				e.printStackTrace();
				threadsToKill.add(thread);
			}
		}
		for (Thread thread : threadsToKill) {
			threads.remove(thread);
		}
		threadsToKill.clear();
		threadsToKill = null;
		ArrayList<Thread> result = new ArrayList<Thread>(threads.size());
		result.addAll(threads);
		return Collections.unmodifiableCollection(result);
	}

	public static synchronized Thread createService(String name,
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

	private synchronized static void kill(Thread thread)
	{
		thread.interrupt();
		threads.remove(thread);
		killedThreads.add(thread);
	}

	public synchronized static void killAll()
	{
		logger.info("Shutting down all services...");

		List<Thread> clonedList = new ArrayList<Thread>(threads.size());
		clonedList.addAll(threads);

		for (Thread thread : clonedList)
		{
			kill(thread);
		}
	}

	public synchronized static boolean isEmpty()
	{
		return threads.isEmpty();
	}
}
