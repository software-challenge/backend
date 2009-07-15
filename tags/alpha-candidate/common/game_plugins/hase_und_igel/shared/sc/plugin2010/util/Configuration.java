package sc.plugin2010.util;

import com.thoughtworks.xstream.XStream;

public class Configuration
{
	private static XStream	xStream;
	
	static 
	{
		xStream = new XStream();
		xStream.setClassLoader(Configuration.class.getClassLoader());
	}

	public static XStream getXStream()
	{
		return xStream;
	}

	public static void setXStream(XStream xstream)
	{
		xStream = xstream;
	}
}
