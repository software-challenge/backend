package sc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ServiceManager
{
	private static Set<Thread> threads = new HashSet<Thread>();
	
	public static Thread createThread(Runnable target)
	{
		return createThread(target, true);
	}
	
	public static Thread createThread(Runnable target, boolean daemon)
	{
		Thread thread = new Thread(target);
		thread.setDaemon(daemon);
		threads.add(thread);
		return thread;
	}
	
	public static void killAll()
	{
		for(Thread thread : threads)
		{
			thread.interrupt();
		}
	}
	
	public static Collection<Thread> getAll()
	{
		return Collections.unmodifiableCollection(threads);
	}
}
