package sc.helpers;

import java.net.URL;
import java.net.URLClassLoader;

public class RuntimeJarLoader extends URLClassLoader
{
	public RuntimeJarLoader(URL[] urls)
	{
		super(urls);
	}

	public RuntimeJarLoader(ClassLoader classLoader)
	{
		super(new URL[] {}, classLoader);
	}

	@Override
	/** 
	 * add classpath to the loader. 
	 */
	public void addURL(URL url)
	{
		super.addURL(url);
	}
}
